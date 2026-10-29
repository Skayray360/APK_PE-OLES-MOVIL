package com.example.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "USUARIO_ESTANDAR",
    val notificationsEnabled: Boolean = true,
    val preferredCategories: List<String> = listOf("Sostenibilidad", "Innovación", "Seguridad"),
    val language: String = "es-MX",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class NewsArticle(
    @DocumentId val id: String = "",
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val author: String = "",
    val status: String = "published",
    val sourceUrl: String = "https://www.penoles.com.mx/",
    val readingTimeMinutes: Int = 4,
    val tags: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Banner(
    @DocumentId val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val targetType: String = "NEWS",
    val targetId: String = "",
    val priority: Int = 1,
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    val imageUrl: String = "",
    val sku: String = "",
    val price: Double = 0.0,
    val currency: String = "MXN",
    val stock: Int = 0,
    val variants: List<String> = emptyList(),
    val active: Boolean = true,
    val productType: String = "PROMOTIONAL", // PROMOTIONAL or INDUSTRIAL
    val technicalSpecs: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CartItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val variant: String = "",
    val quantity: Int = 1,
    val unitPriceSnapshot: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FavoriteItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val targetId: String = "",
    val targetType: String = "NEWS", // NEWS or PRODUCT
    val title: String = "",
    val category: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemsSummary: String = "",
    val itemCount: Int = 1,
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "MXN",
    val status: String = "CONFIRMADO_DEMO",
    val shippingAddress: String = "",
    val shippingMethod: String = "Envío Estándar",
    val paymentReference: String = "",
    val isDemoOrder: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class QuoteRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val companyName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val estimatedVolume: String = "",
    val applicationNotes: String = "",
    val status: String = "PENDIENTE",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class ChatAction(
    val label: String,
    val routeType: String, // NEWS_DETAIL, PRODUCT_DETAIL, TAB_NEWS, TAB_STORE
    val targetId: String = ""
)

data class ChatMessage(
    val id: String,
    val role: String, // "user" or "assistant"
    val text: String,
    val timestampFormatted: String,
    val citations: List<String> = emptyList(),
    val relatedNews: List<NewsArticle> = emptyList(),
    val relatedProducts: List<Product> = emptyList(),
    val relatedActions: List<ChatAction> = emptyList(),
    val requiresHumanSupport: Boolean = false
)

data class CorporateFaq(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val sourceRef: String
)

object PenolesReferenceCatalog {
    val newsCategories = listOf(
        "Todas",
        "Corporativo",
        "Operaciones",
        "Innovación",
        "Sostenibilidad",
        "Medio ambiente",
        "Seguridad",
        "Comunidad",
        "Cultura organizacional",
        "Eventos",
        "Comunicados"
    )

    val quickHomeCategories = listOf(
        "Noticias",
        "Innovación",
        "Sostenibilidad",
        "Seguridad",
        "Comunidad",
        "Eventos"
    )

    val referenceBanners = listOf(
        Banner(
            id = "banner_sostenibilidad_agua",
            title = "Innovación en gestión hídrica y uso de agua tratada en operaciones metalúrgicas",
            subtitle = "Compromiso permanente con la eficiencia hídrica y el equilibrio ambiental en el norte de México.",
            category = "Sostenibilidad",
            imageUrl = "img_banner_sustainability",
            targetType = "NEWS",
            targetId = "news_gestion_hidrica_2026",
            priority = 1,
            active = true
        ),
        Banner(
            id = "banner_transformacion_digital",
            title = "Operaciones inteligentes y telemetría aplicada a la seguridad industrial",
            subtitle = "Tecnología de vanguardia y analítica preventiva en nuestras unidades mineras y metalúrgicas.",
            category = "Innovación",
            imageUrl = "img_banner_mining_innovation",
            targetType = "NEWS",
            targetId = "news_innovacion_seguridad_2026",
            priority = 2,
            active = true
        ),
        Banner(
            id = "banner_catalogo_industrial",
            title = "Catálogo Corporativo de Metales Afinados y Químicos Industriales",
            subtitle = "Consulta especificaciones técnicas de Plata, Zinc SHG, Oro y Sulfato de Sodio con canal de cotización empresarial.",
            category = "Catálogo Industrial",
            imageUrl = "img_industrial_silver_zinc",
            targetType = "STORE",
            targetId = "ind_plata_afinada",
            priority = 3,
            active = true
        )
    )

    val referenceNews = listOf(
        NewsArticle(
            id = "news_gestion_hidrica_2026",
            title = "Innovación en gestión hídrica y recirculación en complejos metalúrgicos",
            summary = "Las plantas de tratamiento de aguas residuales urbanas permiten sustituir el uso de agua fresca en procesos industriales estratégicos.",
            content = """
                ## Eficiencia hídrica e infraestructura circular
                En Industrias Peñoles, el cuidado del agua constituye un eje prioritario de la estrategia de desarrollo sostenible. A través de plantas tratadoras y sistemas cerrados de recirculación, se impulsa el aprovechamiento de aguas residuales tratadas en los procesos metalúrgicos y químicos.
                
                ### Principales líneas de acción
                1. **Recirculación en procesos**: Optimización continua de circuitos cerrados de enfriamiento y lixiviación para minimizar pérdidas por evaporación.
                2. **Tratamiento de aguas municipales**: Aprovechamiento de agua tratada proveniente de zonas urbanas para uso industrial, preservando los acuíferos para el consumo humano.
                3. **Monitoreo ambiental continuo**: Verificación permanente de parámetros físico-químicos conforme a la normatividad mexicana e internacional.
                
                ### Compromiso con las comunidades
                Estas iniciativas se complementan con jornadas de reforestación con especies endémicas del semidesierto y programas educativos ambientales en las comunidades aledañas a las operaciones.
            """.trimIndent(),
            imageUrl = "img_banner_sustainability",
            categoryId = "Sostenibilidad",
            author = "Comunicación Institucional (Contenido de Referencia Editorial)",
            status = "published",
            sourceUrl = "https://www.penoles.com.mx/",
            readingTimeMinutes = 4,
            tags = listOf("Sostenibilidad", "Agua", "Medio ambiente", "Metalurgia")
        ),
        NewsArticle(
            id = "news_innovacion_seguridad_2026",
            title = "Tecnología y cultura preventiva: pilares de la seguridad operativa en unidades mineras",
            summary = "La integración de monitoreo geotécnico en tiempo real, simuladores de capacitación y protocolos de Cero Accidentes fortalece la seguridad del personal.",
            content = """
                ## La seguridad como valor no negociable
                La integridad física y la salud ocupacional de los colaboradores y contratistas representan la máxima prioridad en cada unidad operativa y planta metalúrgica.
                
                ### Innovación aplicada a la prevención
                * **Monitoreo y telemetría en tiempo real**: Sensores de ventilación, proximidad de equipos pesados y estabilidad geotécnica en interior de mina.
                * **Capacitación inmersiva**: Entrenamiento práctico mediante simuladores de operación y talleres de identificación de riesgos críticos.
                * **Cultura de liderazgo visible**: Auditorías preventivas en campo y comités mixtos de seguridad e higiene enfocados en el control de riesgos operativos.
            """.trimIndent(),
            imageUrl = "img_banner_mining_innovation",
            categoryId = "Seguridad",
            author = "Dirección de Seguridad y Medio Ambiente (Referencia)",
            status = "published",
            sourceUrl = "https://www.penoles.com.mx/",
            readingTimeMinutes = 5,
            tags = listOf("Seguridad", "Innovación", "Operaciones", "Tecnología")
        ),
        NewsArticle(
            id = "news_comunidad_rob_2026",
            title = "Impulso al talento joven mediante torneos de robótica, ciencia y educación comunitaria",
            summary = "Programas educativos, becas de excelencia y el fomento a las disciplinas STEAM fortalecen el tejido social en las regiones mineras.",
            content = """
                ## Educación y desarrollo comunitario a largo plazo
                El vínculo con las comunidades donde operamos se refleja en programas permanentes de educación, cultura, deporte y emprendimiento local.
                
                ### Formación en ciencia y tecnología (STEAM)
                El apoyo a equipos juveniles de robótica promueve competencias en ingeniería, programación, trabajo colaborativo y liderazgo entre estudiantes de nivel medio superior en el norte y centro del país.
                
                ### Museo de los Metales y divulgación científica
                A través de exposiciones interactivas, talleres escolares y seminarios para docentes, se acerca el conocimiento sobre las ciencias de la Tierra y el papel esencial de los minerales en la vida cotidiana y la transición energética.
            """.trimIndent(),
            imageUrl = "img_banner_sustainability",
            categoryId = "Comunidad",
            author = "Vinculación Comunitaria (Referencia Editorial)",
            status = "published",
            sourceUrl = "https://www.penoles.com.mx/",
            readingTimeMinutes = 4,
            tags = listOf("Comunidad", "Educación", "Eventos", "Cultura organizacional")
        ),
        NewsArticle(
            id = "news_energia_limpia_2026",
            title = "Transición energética y eficiencia eléctrica en complejos metalúrgicos y químicos",
            summary = "Avances en el abastecimiento con fuentes renovables y modernización de hornos y celdas electrolíticas para reducir la huella de carbono.",
            content = """
                ## Descarbonización y eficiencia energética
                La industria minero-metalúrgica desempeña un papel doble en la transición energética: provee los metales indispensables para las tecnologías limpias y transforma sus propios procesos para disminuir las emisiones de gases de efecto invernadero.
                
                ### Estrategia climática
                * Incorporación de energía eólica y solar en la matriz de consumo eléctrico industrial.
                * Sistemas de cogeneración y recuperación de calor residual en plantas químicas y fundiciones.
                * Medición rigurosa de emisiones de Alcance 1 y 2 bajo estándares internacionales.
            """.trimIndent(),
            imageUrl = "img_banner_mining_innovation",
            categoryId = "Medio ambiente",
            author = "Sostenibilidad y Energía (Referencia Editorial)",
            status = "published",
            sourceUrl = "https://www.penoles.com.mx/",
            readingTimeMinutes = 3,
            tags = listOf("Medio ambiente", "Sostenibilidad", "Innovación", "Operaciones")
        ),
        NewsArticle(
            id = "news_evento_simposio_2026",
            title = "Convocatoria al Simposio de Innovación Metalúrgica y Excelencia Operativa",
            summary = "Encuentro técnico anual para compartir mejores prácticas en metalurgia extractiva, automatización de plantas y gestión ambiental.",
            content = """
                ## Intercambio de conocimiento técnico
                Especialistas en ingeniería química, metalurgia, geología y mantenimiento predictivo participarán en mesas técnicas dedicadas a la mejora continua y la excelencia operativa.
                
                ### Ejes temáticos del encuentro
                1. Automatización avanzada y control estadístico de procesos.
                2. Economía circular de subproductos metalúrgicos.
                3. Seguridad industrial en maniobras de alto riesgo.
            """.trimIndent(),
            imageUrl = "img_industrial_silver_zinc",
            categoryId = "Eventos",
            author = "Comité Técnico Institucional (Referencia)",
            status = "published",
            sourceUrl = "https://www.penoles.com.mx/",
            readingTimeMinutes = 3,
            tags = listOf("Eventos", "Innovación", "Corporativo", "Operaciones")
        )
    )

    val referenceProducts = listOf(
        // A. Tienda de artículos promocionales (Modo demostración administrable)
        Product(
            id = "promo_termo_acero",
            name = "Termo Térmico Institucional de Acero Inoxidable 600 ml",
            description = "Termo de doble pared con aislamiento al vacío en acabado grafito mate y detalle institucional en rojo. Conserva bebidas frías hasta 18 h y calientes hasta 8 h. Catálogo demostrativo.",
            categoryId = "Termos",
            imageUrl = "img_promo_store_kit",
            sku = "PEN-PROMO-TRM-01",
            price = 450.0,
            currency = "MXN",
            stock = 35,
            variants = listOf("Grafito Mate 600ml", "Acero Cepillado 600ml"),
            active = true,
            productType = "PROMOTIONAL",
            technicalSpecs = "Acero inoxidable grado alimenticio 304, libre de BPA, tapa hermética antiderrames."
        ),
        Product(
            id = "promo_gorra_institucional",
            name = "Gorra Estructurada Conmemorativa Edición Conecta",
            description = "Gorra de seis paneles en sarga de algodón peinado de alta densidad con ajuste metálico trasero y bordado institucional de alta precisión.",
            categoryId = "Gorras",
            imageUrl = "img_promo_store_kit",
            sku = "PEN-PROMO-GOR-02",
            price = 320.0,
            currency = "MXN",
            stock = 50,
            variants = listOf("Unitalla Carbón", "Unitalla Negro"),
            active = true,
            productType = "PROMOTIONAL",
            technicalSpecs = "100% algodón peinado de 280 g/m², ojales bordados de ventilación, hebilla metálica."
        ),
        Product(
            id = "promo_libreta_ejecutiva",
            name = "Libreta Ejecutiva Pasta Dura con Cierre Elástico",
            description = "Cuaderno profesional de 192 páginas en papel marfil de 100 g/m² certificado FSC, cubierta texturizada gris antracita, separador textil y bolsillo interior.",
            categoryId = "Libretas",
            imageUrl = "img_promo_store_kit",
            sku = "PEN-PROMO-LIB-03",
            price = 280.0,
            currency = "MXN",
            stock = 60,
            variants = listOf("Raya A5", "Puntos A5"),
            active = true,
            productType = "PROMOTIONAL",
            technicalSpecs = "Formato A5 (14.5 x 21 cm), 96 hojas punteadas/rayadas, papel libre de ácido."
        ),
        Product(
            id = "promo_playera_polo",
            name = "Playera Tipo Polo Institucional Piqué Transpirable",
            description = "Prenda corporativa confeccionada en tejido piqué de algodón y poliéster con tecnología de secado rápido y cuello ribeteado.",
            categoryId = "Playeras",
            imageUrl = "img_promo_store_kit",
            sku = "PEN-PROMO-PLY-04",
            price = 520.0,
            currency = "MXN",
            stock = 28,
            variants = listOf("CH", "M", "G", "XG"),
            active = true,
            productType = "PROMOTIONAL",
            technicalSpecs = "60% algodón / 40% poliéster técnico, costuras reforzadas en hombros."
        ),
        Product(
            id = "promo_mochila_ejecutiva",
            name = "Mochila Ejecutiva Porta Laptop 15.6\" Repelente al Agua",
            description = "Mochila ergonómica con compartimento acolchado para equipo de cómputo, organizador frontal, respaldo transpirable y detalles reflectantes de seguridad.",
            categoryId = "Mochilas",
            imageUrl = "img_promo_store_kit",
            sku = "PEN-PROMO-MOC-05",
            price = 980.0,
            currency = "MXN",
            stock = 18,
            variants = listOf("Negro/Grafito"),
            active = true,
            productType = "PROMOTIONAL",
            technicalSpecs = "Poliéster balístico 900D repelente al agua, capacidad 24 litros, cierres reforzados."
        ),

        // B. Catálogo corporativo de productos industriales (Exclusivamente informativo y sujeto a cotización)
        Product(
            id = "ind_plata_afinada",
            name = "Plata Afinada en Granalla y Barras (Pureza 99.99%)",
            description = "Plata refinada de alta pureza para aplicaciones eléctricas, electrónicas, fotovoltaicas, acuñación, joyería industrial y procesos químicos especializados. Exclusivo para consulta técnica y solicitud de cotización empresarial.",
            categoryId = "Metales Afinados",
            imageUrl = "img_industrial_silver_zinc",
            sku = "PEN-IND-AG-9999",
            price = 0.0,
            currency = "MXN",
            stock = 0,
            variants = listOf("Barras (~1,000 oz troy)", "Granalla en cuñetes de 25 kg"),
            active = true,
            productType = "INDUSTRIAL",
            technicalSpecs = "Pureza mínima: 99.99% Ag. Certificación internacional Good Delivery (LBMA). Aplicaciones: celdas solares, contactos eléctricos, catalizadores."
        ),
        Product(
            id = "ind_zinc_shg",
            name = "Zinc Afinado Special High Grade (SHG 99.995%)",
            description = "Zinc electrolítico de ultra alta pureza utilizado en galvanización por inmersión en caliente para la industria automotriz y de construcción, producción de latón y aleaciones Zamak.",
            categoryId = "Metales Afinados",
            imageUrl = "img_industrial_silver_zinc",
            sku = "PEN-IND-ZN-SHG",
            price = 0.0,
            currency = "MXN",
            stock = 0,
            variants = listOf("Lingotes de 25 kg", "Jumbos de 1,000 kg", "Aleaciones para galvanizado"),
            active = true,
            productType = "INDUSTRIAL",
            technicalSpecs = "Grado SHG (Special High Grade) >= 99.995% Zn conforme a norma ASTM B6. Registro en LME (London Metal Exchange)."
        ),
        Product(
            id = "ind_sulfato_sodio",
            name = "Sulfato de Sodio Anhidro de Alta Pureza",
            description = "Insumo químico inorgánico natural de alta blancura y pureza empleado en la fabricación de detergentes en polvo, industria del vidrio plano, papel Kraft y teñido textil.",
            categoryId = "Químicos Industriales",
            imageUrl = "img_industrial_silver_zinc",
            sku = "PEN-IND-NA2SO4",
            price = 0.0,
            currency = "MXN",
            stock = 0,
            variants = listOf("Sacos de 50 kg", "Súper sacos de 1,000 kg", "Granel en tolva ferroviaria"),
            active = true,
            productType = "INDUSTRIAL",
            technicalSpecs = "Pureza > 99.3% Na2SO4. Baja humedad libre y granulometría controlada para mezclado homogéneo."
        ),
        Product(
            id = "ind_oxido_magnesio",
            name = "Óxido de Magnesio (Magnesia Caústica, Sinterizada y Electrofundida)",
            description = "Especialidades de óxido de magnesio para ladrillos refractarios de la industria siderúrgica, aislamiento eléctrico tubular, suplementación mineral y tratamiento ambiental.",
            categoryId = "Químicos Industriales",
            imageUrl = "img_industrial_silver_zinc",
            sku = "PEN-IND-MGO",
            price = 0.0,
            currency = "MXN",
            stock = 0,
            variants = listOf("Grado Refractario", "Grado Eléctrico", "Grado Cáustico Industrial"),
            active = true,
            productType = "INDUSTRIAL",
            technicalSpecs = "Alta densidad aparente y resistencia a la escoria básica en hornos metalúrgicos."
        )
    )

    val corporateFaqs = listOf(
        CorporateFaq(
            id = "faq_quienes_somos",
            question = "¿Dónde consulto información corporativa de Industrias Peñoles?",
            answer = "Industrias Peñoles es un grupo minero-metalúrgico y químico mexicano integrado con operaciones en exploración, minería, metalurgia y productos químicos. Puedes consultar informes anuales, gobierno corporativo y sostenibilidad en el sitio oficial https://www.penoles.com.mx/ y en la sección Corporativo dentro de Noticias.",
            category = "Corporativo",
            sourceRef = "Portal Institucional penoles.com.mx — Perfil Corporativo"
        ),
        CorporateFaq(
            id = "faq_contacto",
            question = "¿Cómo puedo contactar a Peñoles o solicitar atención comercial?",
            answer = "Para productos industriales (metales afinados y químicos), utiliza el botón 'Solicitar cotización' en cada ficha del Catálogo Industrial dentro de la sección Tienda. Para atención institucional general y relación con inversionistas o proveedores, consulta los canales oficiales publicados en https://www.penoles.com.mx/.",
            category = "Contacto",
            sourceRef = "Canales Oficiales de Contacto — penoles.com.mx"
        ),
        CorporateFaq(
            id = "faq_sostenibilidad",
            question = "¿Cuáles son las acciones de sostenibilidad y cuidado del agua?",
            answer = "La estrategia de sostenibilidad prioriza la recirculación hídrica, el uso de aguas residuales municipales tratadas en procesos industriales, la transición hacia energías renovables, la reforestación y programas educativos como robótica juvenil y el Museo de los Metales.",
            category = "Sostenibilidad",
            sourceRef = "Informe de Desarrollo Sostenible — Ejes Ambientales y Sociales"
        ),
        CorporateFaq(
            id = "faq_tienda_modalidades",
            question = "¿Cuál es la diferencia entre los artículos promocionales y el catálogo industrial?",
            answer = "La Tienda cuenta con dos modalidades: 1) Artículos Promocionales (termos, gorras, libretas, playeras y mochilas en modo demostración con carrito) y 2) Catálogo Industrial (Plata, Zinc SHG, Sulfato de Sodio y Óxido de Magnesio), el cual funciona exclusivamente como catálogo técnico informativo y canal de solicitud de cotización empresarial, sin venta directa en carrito.",
            category = "Tienda",
            sourceRef = "Reglas de Operación Comercial — Peñoles Conecta"
        )
    )
}
