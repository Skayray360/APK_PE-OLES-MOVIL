const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- THE DIRTY DOZEN & CLIENT QUERY ALIGNMENT TESTS ---

test("1. Unauthenticated user: cannot read orders or cart_items", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("orders").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("cart_items").get());
});

test("2. Cross-user isolation: Alice cannot read Bob's cart_items or orders", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("cart_items").doc("item_1").set({
      userId: BOB_UID,
      productId: "prod_termo",
      productName: "Termo Institucional",
      variant: "Negro Mate",
      quantity: 2,
      unitPriceSnapshot: 450.0,
      createdAt: now,
      updatedAt: now,
    });
    await context.firestore().collection("orders").doc("order_bob").set({
      userId: BOB_UID,
      itemsSummary: "2x Termo Institucional",
      itemCount: 2,
      subtotal: 900.0,
      shipping: 120.0,
      total: 1020.0,
      currency: "MXN",
      status: "CONFIRMADO_DEMO",
      shippingAddress: "Av. Metalúrgica 550, Torreón, Coahuila",
      shippingMethod: "Envío Estándar",
      paymentReference: "DEMO-REF-001",
      isDemoOrder: true,
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("cart_items").doc("item_1").get());
  await assertFails(aliceDb.collection("orders").doc("order_bob").get());
});

test("3. Role escalation prevention: Standard user cannot self-assign ADMINISTRADOR role on profile creation", async () => {
  const now = new Date();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@example.com",
      role: "ADMINISTRADOR",
      notificationsEnabled: true,
      language: "es-MX",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@example.com",
      role: "USUARIO_ESTANDAR",
      notificationsEnabled: true,
      language: "es-MX",
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("4. Shadow update / Ghost field rejection: Cannot inject unauthorized field into user profile", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@example.com",
      role: "USUARIO_ESTANDAR",
      notificationsEnabled: true,
      language: "es-MX",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      displayName: "Alice Mod",
      isVerified: true,
      updatedAt: new Date(),
    })
  );
});

test("5. Backend order total validation: Rejects order where total != subtotal + shipping", async () => {
  const now = new Date();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Tampered total (subtotal 900 + shipping 120 != 50)
  await assertFails(
    aliceDb.collection("orders").doc("order_tampered").set({
      userId: ALICE_UID,
      itemsSummary: "2x Termo Institucional",
      itemCount: 2,
      subtotal: 900.0,
      shipping: 120.0,
      total: 50.0,
      currency: "MXN",
      status: "CONFIRMADO_DEMO",
      shippingAddress: "Av. Metalúrgica 550, Torreón, Coahuila",
      shippingMethod: "Envío Estándar",
      paymentReference: "DEMO-REF-002",
      isDemoOrder: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Valid total (900 + 120 == 1020)
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_valid").set({
      userId: ALICE_UID,
      itemsSummary: "2x Termo Institucional",
      itemCount: 2,
      subtotal: 900.0,
      shipping: 120.0,
      total: 1020.0,
      currency: "MXN",
      status: "CONFIRMADO_DEMO",
      shippingAddress: "Av. Metalúrgica 550, Torreón, Coahuila",
      shippingMethod: "Envío Estándar",
      paymentReference: "DEMO-REF-002",
      isDemoOrder: true,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("6. Email spoofing guard: Unverified admin email cannot create news articles", async () => {
  const now = new Date();
  const spoofedAdminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "aldo.jael123@gmail.com",
    email_verified: false,
  }).firestore();

  await assertFails(
    spoofedAdminDb.collection("news_articles").doc("news_1").set({
      title: "Noticia Corporativa",
      summary: "Resumen válido de noticia corporativa.",
      content: "Contenido completo de la noticia corporativa con suficiente longitud.",
      imageUrl: "img_banner_mining_innovation",
      categoryId: "Innovación",
      author: "Comunicación Corporativa",
      status: "published",
      sourceUrl: "https://www.penoles.com.mx",
      readingTimeMinutes: 4,
      createdAt: now,
      updatedAt: now,
    })
  );

  const verifiedAdminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "aldo.jael123@gmail.com",
    email_verified: true,
  }).firestore();

  await assertSucceeds(
    verifiedAdminDb.collection("news_articles").doc("news_1").set({
      title: "Noticia Corporativa",
      summary: "Resumen válido de noticia corporativa.",
      content: "Contenido completo de la noticia corporativa con suficiente longitud.",
      imageUrl: "img_banner_mining_innovation",
      categoryId: "Innovación",
      author: "Comunicación Corporativa",
      status: "published",
      sourceUrl: "https://www.penoles.com.mx",
      readingTimeMinutes: 4,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("7. Client query alignment: Authenticated user can query own orders with userId filter, fails without filter", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("order_alice").set({
      userId: ALICE_UID,
      itemsSummary: "1x Gorra Institucional",
      itemCount: 1,
      subtotal: 320.0,
      shipping: 99.0,
      total: 419.0,
      currency: "MXN",
      status: "CONFIRMADO_DEMO",
      shippingAddress: "Torreón, Coahuila",
      shippingMethod: "Envío Estándar",
      paymentReference: "DEMO-REF-003",
      isDemoOrder: true,
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("orders").where("userId", "==", ALICE_UID).get());
  await assertFails(aliceDb.collection("orders").get());
});

test("8. Industrial quote requests: Authenticated user can create and query own quote requests", async () => {
  const now = new Date();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("quote_requests").doc("quote_1").set({
      userId: ALICE_UID,
      productId: "ind_zinc_shg",
      productName: "Zinc Afinado SHG 99.995%",
      companyName: "Manufacturas del Norte S.A. de C.V.",
      contactEmail: "compras@manufacturasnorte.mx",
      contactPhone: "+52 871 729 5500",
      estimatedVolume: "25 Toneladas métricas / mes",
      applicationNotes: "Galvanización por inmersión en caliente para estructuras automotrices.",
      status: "PENDIENTE",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("quote_requests").where("userId", "==", ALICE_UID).get()
  );
});
