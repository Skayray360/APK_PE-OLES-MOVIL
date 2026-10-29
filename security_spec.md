# Especificación de Seguridad Firestore — Peñoles Conecta (Security TDD)

## 1. Invariantes de Datos (Data Invariants)
1. **Aislamiento de Identidad (`userId == request.auth.uid`)**: Ningún documento de usuario (`users`), carrito (`cart_items`), favoritos (`favorites`), pedidos (`orders`) o cotizaciones industriales (`quote_requests`) puede crearse con un `userId` distinto al UID autenticado.
2. **Protección contra Escalación de Privilegios (RBAC)**: Al registrarse en `/users/{userId}`, un usuario estándar únicamente puede asignarse `role == 'USUARIO_ESTANDAR'`. El campo `role` es inmutable para el propio usuario durante actualizaciones (`incoming().role == existing().role`).
3. **Validación Matemática de Totales en Pedidos (`orders`)**: Todo pedido debe cumplir en las reglas de seguridad que `incoming().total == incoming().subtotal + incoming().shipping`, `subtotal > 0`, `shipping >= 0`, y `isDemoOrder == true`.
4. **Separación Estricta de Productos Industriales (`quote_requests`)**: Los productos industriales sujetos a cotización solo se canalizan mediante `/quote_requests/{quoteId}` con estado inicial `'PENDIENTE'`, nunca mediante órdenes de compra directa sin validación.
5. **Integridad Temporal**: Todos los campos `createdAt` y `updatedAt` deben ser `timestamp` y cumplir `<= request.time`. En actualizaciones, `createdAt` es inmutable (`incoming().createdAt == existing().createdAt`).
6. **Bloqueo de Estado Terminal**: Los pedidos con estado `'ENTREGADO'` o `'CANCELADO'` y las cotizaciones con estado `'CERRADA'` no pueden ser modificados por usuarios estándar.
7. **Alineación de Consultas (`allow list`)**: Ninguna colección permite lecturas masivas sin filtro (`allow list` evalúa `existing().userId == request.auth.uid`, `existing().status == 'published'` o `existing().active == true`), sin llamadas `get()`/`exists()` dentro de `allow list`.

## 2. The "Dirty Dozen" Payloads (Pruebas Adversarias)
1. **Unauthenticated Read (`orders`)**: Lectura sin sesión en `/orders` -> RECHAZADO.
2. **Cross-User Read (`cart_items`)**: Alice intenta leer `/users/bob_456/cart_items/item_1` -> RECHAZADO.
3. **Identity Spoofing on Create (`orders`)**: Alice intenta crear un pedido con `userId: "bob_456"` -> RECHAZADO.
4. **Role Escalation on Profile Create (`users`)**: Alice intenta crearse perfil con `role: "ADMINISTRADOR"` -> RECHAZADO.
5. **Shadow Update / Ghost Field (`users`)**: Alice intenta actualizar su perfil inyectando `isVerified: true` -> RECHAZADO por `affectedKeys().hasOnly(...)` y `keys().hasOnly(...)`.
6. **Price Manipulation (`orders`)**: Alice envía `subtotal: 500, shipping: 100, total: 50` -> RECHAZADO por `total == subtotal + shipping`.
7. **Terminal State Bypass (`orders`)**: Intentar actualizar un pedido cuyo estado actual ya es `'CANCELADO'` -> RECHAZADO.
8. **Immortal Field Mutation (`cart_items`)**: Intentar modificar `createdAt` o `productId` en un ítem del carrito existente -> RECHAZADO.
9. **Resource Poisoning / Oversized String (`quote_requests`)**: Enviar `applicationNotes` de más de 1000 caracteres -> RECHAZADO.
10. **Email Spoofing Admin (`news_articles`)**: Usuario con email de admin pero `email_verified: false` intenta publicar una noticia -> RECHAZADO.
11. **Unfiltered List Query (`orders`)**: Usuario autenticado ejecuta `db.collection("orders").get()` sin `.where("userId", "==", uid)` -> RECHAZADO.
12. **Standard User CMS Write (`products`)**: Usuario estándar intenta crear o modificar inventario en `/products` -> RECHAZADO.
