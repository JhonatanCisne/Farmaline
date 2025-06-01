// Base de datos de productos
const products = [
  {
    id: 1,
    name: "Amoxicilina 250 mg",
    fullName: "Amoxicilina 250 mg. solución oral",
    category: "medicamentos",
    price: 11.5,
    image: "../images/P/Amoxicilina 250 mg.webp",
    description: "Antibiótico de amplio espectro utilizado para tratar diversas infecciones bacterianas.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: [
      "Presentación: Frasco de 60ml",
      "Concentración: 250mg/5ml",
      "Vía de administración: Oral",
      "Requiere receta médica",
    ],
    comments: [
      {
        author: "María González",
        rating: 5,
        text: "Excelente producto, muy efectivo para tratar infecciones.",
        date: "2024-01-15",
      },
    ],
  },
  {
    id: 2,
    name: "Paracetamol 500 mg",
    fullName: "Paracetamol 500 mg. Tabletas",
    category: "medicamentos",
    price: 6.5,
    image: "../images/P/Paracetamol 500 mg.webp",
    description: "Analgésico y antipirético para el alivio del dolor y la fiebre.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: [
      "Presentación: Caja con 20 tabletas",
      "Concentración: 500mg por tableta",
      "Vía de administración: Oral",
      "Venta libre",
    ],
    comments: [
      {
        author: "Carlos Ruiz",
        rating: 4,
        text: "Muy bueno para el dolor de cabeza, actúa rápido.",
        date: "2024-01-10",
      },
    ],
  },
  {
    id: 3,
    name: "Ibuprofeno 800 mg",
    fullName: "Ibuprofeno 800 mg",
    category: "medicamentos",
    price: 9.5,
    image: "../images/P/Ibuprofeno.webp",
    description: "Antiinflamatorio no esteroideo para el tratamiento del dolor e inflamación.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 12.0,
    availability: "venta libre",
    info: [
      "Presentación: Caja con 50 tabletas",
      "Concentración: 100mg por tableta",
      "Vía de administración: Oral",
      "Venta libre",
    ],
    comments: [],
  },
  {
    id: 4,
    name: "Vitamina C 1000mg",
    fullName: "Vitamina C 1000mg - Tabletas efervescentes",
    category: "vitaminas",
    price: 15.9,
    image: "../images/P/Vitamina-c 1000 mg.webp",
    description: "Suplemento vitamínico para fortalecer el sistema inmunológico.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: [
      "Presentación: Tubo con 20 tabletas efervescentes",
      "Concentración: 1000mg por tableta",
      "Sabor: Naranja",
      "Venta libre",
    ],
    comments: [
      {
        author: "Ana López",
        rating: 5,
        text: "Excelente para reforzar las defensas, sabor agradable.",
        date: "2024-01-12",
      },
    ],
  },
  {
    id: 5,
    name: "Shampoo Anticaspa",
    fullName: "Shampoo Anticaspa con Ketoconazol",
    category: "cuidado-cabello",
    price: 18.5,
    image: "../images/P/Shampoo anticaspa.jpg",
    description: "Shampoo especializado para el tratamiento de la caspa y dermatitis seborreica.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 22.0,
    availability: "venta libre",
    info: [
      "Presentación: Frasco de 200ml",
      "Principio activo: Ketoconazol 1%",
      "Uso: 2-3 veces por semana",
      "Venta libre",
    ],
    comments: [],
  },
  {
    id: 6,
    name: "Pañales Talla M",
    fullName: "Pañales desechables Talla M (5-9kg)",
    category: "bebes",
    price: 25.9,
    image: "../images/P/Babysec.jpg",
    description: "Pañales ultra absorbentes con tecnología de gel para máxima protección.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: [
      "Presentación: Paquete de 68 unidades",
      "Talla: M (5-9kg)",
      "Características: Ultra absorbente, hipoalergénico",
      "12 horas de protección",
    ],
    comments: [
      {
        author: "Patricia Morales",
        rating: 4,
        text: "Buenos pañales, muy absorbentes y cómodos para el bebé.",
        date: "2024-01-08",
      },
    ],
  },
  {
    id: 7,
    name: "Protector Solar SPF 50",
    fullName: "Protector Solar Facial SPF 50+",
    category: "fotoproteccion",
    price: 32.9,
    image: "../images/P/Eucerin.jpg",
    description: "Protector solar de amplio espectro para protección facial diaria.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: [
      "Presentación: Tubo de 50ml",
      "Factor de protección: SPF 50+",
      "Tipo: Facial, no comedogénico",
      "Resistente al agua",
    ],
    comments: [],
  },
  {
    id: 8,
    name: "Crema Hidratante",
    fullName: "Crema Hidratante Corporal con Urea",
    category: "cuidado-personal",
    price: 14.9,
    image: "../images/P/Crema eucerin.webp",
    description: "Crema hidratante intensiva para piel seca y muy seca.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 18.9,
    availability: "venta libre",
    info: [
      "Presentación: Tubo de 250ml",
      "Principio activo: Urea 10%",
      "Tipo: Corporal, hipoalergénica",
      "Para piel seca y muy seca",
    ],
    comments: [
      {
        author: "Roberto Silva",
        rating: 5,
        text: "Excelente crema, deja la piel muy suave e hidratada.",
        date: "2024-01-05",
      },
    ],
  },
  {
    id: 9,
    name: "NAN Sin Lactosa",
    fullName: "NAN Sin Lactosa - Fórmula Infantil",
    category: "bebes",
    price: 71.9,
    image: "../images/P/Nan.avif",
    description:
      "Fórmula diseñada especialmente para el manejo dietario de una mala digestión/intolerancia a la lactosa.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Lata de 400g", "Edad: 0-12 meses", "Sin lactosa", "Fácil digestión"],
    comments: [],
  },
  {
    id: 10,
    name: "Cefalexina 500mg",
    fullName: "Cefalexina 500mg - Antibiótico",
    category: "medicamentos",
    price: 18.5,
    image: "../images/P/cefalexina.jpg",
    description: "Antibiótico cefalosporina para infecciones de piel, tracto respiratorio y urinario.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: ["Presentación: Caja con 100 tabletas", "Concentración: 500mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 11,
    name: "Ciprofloxacino 500mg",
    fullName: "Ciprofloxacino 500mg - Antibiótico",
    category: "medicamentos",
    price: 22.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Antibiótico fluoroquinolona para infecciones del tracto urinario y respiratorio.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 28.0,
    availability: "requiere receta",
    info: ["Presentación: Caja con 10 tabletas", "Concentración: 500mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 12,
    name: "Dexametasona 4mg",
    fullName: "Dexametasona 4mg - Corticosteroide",
    category: "medicamentos",
    price: 12.5,
    image: "/placeholder.svg?height=80&width=80",
    description: "Corticosteroide para tratamiento de inflamación y enfermedades autoinmunes.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: ["Presentación: Caja con 20 tabletas", "Concentración: 4mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 13,
    name: "Loratadina 10mg",
    fullName: "Loratadina 10mg - Antihistamínico",
    category: "medicamentos",
    price: 8.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Antihistamínico para rinitis alérgica y urticaria.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Caja con 10 tabletas", "Concentración: 10mg", "Vía: Oral", "Venta libre"],
    comments: [],
  },
  {
    id: 14,
    name: "Losartán 50mg",
    fullName: "Losartán 50mg - Antihipertensivo",
    category: "medicamentos",
    price: 16.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Antihipertensivo para el tratamiento de la hipertensión arterial.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: ["Presentación: Caja con 30 tabletas", "Concentración: 50mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 15,
    name: "Metformina 850mg",
    fullName: "Metformina 850mg - Antidiabético",
    category: "medicamentos",
    price: 19.5,
    image: "/placeholder.svg?height=80&width=80",
    description: "Antidiabético oral para el tratamiento de diabetes mellitus tipo 2.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: ["Presentación: Caja con 30 tabletas", "Concentración: 850mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 16,
    name: "Omeprazol 20mg",
    fullName: "Omeprazol 20mg - Inhibidor bomba protones",
    category: "medicamentos",
    price: 13.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Inhibidor de bomba de protones para úlcera péptica y reflujo gastroesofágico.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Caja con 14 cápsulas", "Concentración: 20mg", "Vía: Oral", "Venta libre"],
    comments: [],
  },
  {
    id: 17,
    name: "Prednisona 20mg",
    fullName: "Prednisona 20mg - Corticosteroide",
    category: "medicamentos",
    price: 11.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Corticosteroide para inflamación, alergias y enfermedades autoinmunes.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: ["Presentación: Caja con 20 tabletas", "Concentración: 20mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 18,
    name: "Salbutamol Inhalador",
    fullName: "Salbutamol Inhalador - Broncodilatador",
    category: "medicamentos",
    price: 24.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Broncodilatador para asma y broncoespasmo.",
    isNew: false,
    hasDiscount: false,
    availability: "requiere receta",
    info: [
      "Presentación: Inhalador 200 dosis",
      "Concentración: 100mcg/dosis",
      "Vía: Inhalatoria",
      "Requiere receta médica",
    ],
    comments: [],
  },
  {
    id: 19,
    name: "Simvastatina 20mg",
    fullName: "Simvastatina 20mg - Hipolipemiante",
    category: "medicamentos",
    price: 21.5,
    image: "/placeholder.svg?height=80&width=80",
    description: "Estatina para el tratamiento de hipercolesterolemia.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 26.0,
    availability: "requiere receta",
    info: ["Presentación: Caja con 30 tabletas", "Concentración: 20mg", "Vía: Oral", "Requiere receta médica"],
    comments: [],
  },
  {
    id: 20,
    name: "Multivitamínico Adulto",
    fullName: "Multivitamínico Adulto - Complejo vitamínico",
    category: "vitaminas",
    price: 28.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Complejo multivitamínico para adultos con vitaminas y minerales esenciales.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco con 60 tabletas", "Vitaminas A, C, D, E, B", "Minerales esenciales", "Venta libre"],
    comments: [],
  },
  {
    id: 21,
    name: "Omega 3",
    fullName: "Omega 3 - Suplemento nutricional",
    category: "vitaminas",
    price: 35.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Suplemento de ácidos grasos omega 3 para salud cardiovascular.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco con 90 cápsulas", "1000mg por cápsula", "EPA y DHA", "Venta libre"],
    comments: [],
  },
  {
    id: 22,
    name: "Leche de Magnesia",
    fullName: "Leche de Magnesia - Antiácido y laxante",
    category: "cuidado-personal",
    price: 9.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Antiácido y laxante suave para acidez estomacal y estreñimiento.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco de 120ml", "Hidróxido de magnesio", "Antiácido y laxante", "Venta libre"],
    comments: [],
  },
  {
    id: 23,
    name: "Toallitas Húmedas Bebé",
    fullName: "Toallitas Húmedas para Bebé - Paquete familiar",
    category: "bebes",
    price: 12.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Toallitas húmedas hipoalergénicas para la limpieza delicada del bebé.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 15.9,
    availability: "venta libre",
    info: ["Presentación: Paquete de 80 toallitas", "Hipoalergénicas", "Con aloe vera", "Venta libre"],
    comments: [],
  },
  {
    id: 24,
    name: "Aceite Johnson's Baby",
    fullName: "Aceite Johnson's Baby - Cuidado infantil",
    category: "bebes",
    price: 16.5,
    image: "/placeholder.svg?height=80&width=80",
    description: "Aceite suave para bebé que hidrata y protege la piel delicada.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco de 200ml", "Hipoalergénico", "Dermatológicamente probado", "Venta libre"],
    comments: [],
  },
  {
    id: 25,
    name: "Shampoo Reparador",
    fullName: "Shampoo Reparador con Keratina",
    category: "cuidado-cabello",
    price: 22.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Shampoo reparador con keratina para cabello dañado y quebradizo.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 27.9,
    availability: "venta libre",
    info: ["Presentación: Frasco de 400ml", "Con keratina hidrolizada", "Para cabello dañado", "Venta libre"],
    comments: [],
  },
  {
    id: 26,
    name: "Acondicionador Nutritivo",
    fullName: "Acondicionador Nutritivo con Argán",
    category: "cuidado-cabello",
    price: 19.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Acondicionador nutritivo con aceite de argán para cabello seco.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco de 300ml", "Con aceite de argán", "Hidratación profunda", "Venta libre"],
    comments: [],
  },
  {
    id: 27,
    name: "Mascarilla Capilar",
    fullName: "Mascarilla Capilar Reconstructora",
    category: "cuidado-cabello",
    price: 32.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Mascarilla capilar reconstructora para tratamiento intensivo.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco de 250ml", "Tratamiento intensivo", "Reconstrucción capilar", "Venta libre"],
    comments: [],
  },
  {
    id: 28,
    name: "Protector Solar Corporal SPF 30",
    fullName: "Protector Solar Corporal SPF 30",
    category: "fotoproteccion",
    price: 28.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Protector solar corporal de amplio espectro para uso diario.",
    isNew: false,
    hasDiscount: false,
    availability: "venta libre",
    info: ["Presentación: Frasco de 200ml", "Factor de protección: SPF 30", "Resistente al agua", "Venta libre"],
    comments: [],
  },
  {
    id: 29,
    name: "Bloqueador Solar Infantil",
    fullName: "Bloqueador Solar Infantil SPF 60",
    category: "fotoproteccion",
    price: 35.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Bloqueador solar especial para niños con fórmula hipoalergénica.",
    isNew: true,
    hasDiscount: false,
    availability: "venta libre",
    info: [
      "Presentación: Tubo de 120ml",
      "Factor de protección: SPF 60",
      "Fórmula hipoalergénica",
      "Especial para niños",
    ],
    comments: [],
  },
  {
    id: 30,
    name: "After Sun Reparador",
    fullName: "After Sun Reparador con Aloe Vera",
    category: "fotoproteccion",
    price: 24.9,
    image: "/placeholder.svg?height=80&width=80",
    description: "Loción after sun reparadora con aloe vera para después de la exposición solar.",
    isNew: false,
    hasDiscount: true,
    originalPrice: 29.9,
    availability: "venta libre",
    info: ["Presentación: Frasco de 200ml", "Con aloe vera natural", "Efecto refrescante", "Venta libre"],
    comments: [],
  },
]

let filteredProducts = [...products]
let currentProduct = null
const bootstrap = window.bootstrap // Declare the bootstrap variable

// Inicializar la página
document.addEventListener("DOMContentLoaded", () => {
  renderProducts()
  setupEventListeners()
})

// Configurar event listeners
function setupEventListeners() {
  // Filtros de precio
  document.querySelectorAll(".price-filter").forEach((radio) => {
    radio.addEventListener("change", applyFilters)
  })

  // Filtro alfabético
  document.querySelector(".alpha-filter").addEventListener("change", applyFilters)

  // Filtros de descuento y nuevos
  document.getElementById("discountFilter").addEventListener("change", applyFilters)
  document.getElementById("newFilter").addEventListener("change", applyFilters)

  // Filtros de categoría
  document.querySelectorAll(".category-filter").forEach((link) => {
    link.addEventListener("click", function (e) {
      e.preventDefault()
      const category = this.dataset.category
      filterByCategory(category)
    })
  })

  // Búsqueda
  document.getElementById("searchInput").addEventListener("input", function () {
    const searchTerm = this.value.toLowerCase()
    searchProducts(searchTerm)
  })

  // Modal events
  setupModalEvents()
}

// Renderizar productos
function renderProducts() {
  const container = document.getElementById("productsContainer")
  container.innerHTML = ""

  filteredProducts.forEach((product) => {
    const productCard = createProductCard(product)
    container.appendChild(productCard)
  })
}

// Crear tarjeta de producto
function createProductCard(product) {
  const col = document.createElement("div")
  col.className = "col-md-4 mb-4"

  const badges = []
  if (product.isNew) badges.push('<span class="badge badge-new me-1">NUEVO</span>')
  if (product.hasDiscount) badges.push('<span class="badge badge-discount me-1">DESCUENTO</span>')

  const priceHTML = product.hasDiscount
    ? `<p class="h5 fw-bold mb-0">S/. ${product.price.toFixed(2)} <small class="text-muted text-decoration-line-through">S/. ${product.originalPrice.toFixed(2)}</small></p>`
    : `<p class="h5 fw-bold mb-0">S/. ${product.price.toFixed(2)}</p>`

  col.innerHTML = `
        <div class="card h-100 product-card" data-product-id="${product.id}">
            <div class="card-body">
                <div class="mb-3">
                    <span class="badge bg-danger mb-2">${product.name.split(" ")[0].toUpperCase()}:</span>
                    ${badges.join("")}
                </div>
                <div class="d-flex align-items-center mb-3">
                    <img src="${product.image}" alt="${product.name}" class="me-3" style="width: 80px; height: 80px; object-fit: contain;">
                    <div>
                        <h6 class="card-title small mb-1">${product.fullName}</h6>
                        ${priceHTML}
                    </div>
                </div>
                <button class="btn btn-teal w-100 rounded-pill add-to-cart-btn" data-product-id="${product.id}">AÑADIR</button>
            </div>
        </div>
    `

  // Event listener para abrir modal
  col.querySelector(".product-card").addEventListener("click", (e) => {
    if (!e.target.classList.contains("add-to-cart-btn")) {
      openProductModal(product)
    }
  })

  // Event listener para botón añadir
  col.querySelector(".add-to-cart-btn").addEventListener("click", (e) => {
    e.stopPropagation()
    addToCart(product)
  })

  return col
}

// Aplicar filtros
function applyFilters() {
  let filtered = [...products]

  // Filtro de categoría actual
  const currentCategory = getCurrentCategory()
  if (currentCategory && currentCategory !== "all") {
    filtered = filtered.filter((product) => product.category === currentCategory)
  }

  // Filtro de descuento
  if (document.getElementById("discountFilter").checked) {
    filtered = filtered.filter((product) => product.hasDiscount)
  }

  // Filtro de nuevos
  if (document.getElementById("newFilter").checked) {
    filtered = filtered.filter((product) => product.isNew)
  }

  // Ordenamiento
  const selectedSort = document.querySelector('input[name="priceFilter"]:checked').value

  switch (selectedSort) {
    case "asc":
      filtered.sort((a, b) => a.price - b.price)
      break
    case "desc":
      filtered.sort((a, b) => b.price - a.price)
      break
    case "alpha":
      filtered.sort((a, b) => a.name.localeCompare(b.name))
      break
  }

  filteredProducts = filtered
  renderProducts()
}

// Filtrar por categoría
function filterByCategory(category) {
  setCurrentCategory(category)
  applyFilters()
}

// Búsqueda de productos
function searchProducts(searchTerm) {
  if (!searchTerm) {
    applyFilters()
    return
  }

  filteredProducts = products.filter(
    (product) =>
      product.name.toLowerCase().includes(searchTerm) ||
      product.fullName.toLowerCase().includes(searchTerm) ||
      product.description.toLowerCase().includes(searchTerm),
  )

  renderProducts()
}

// Obtener categoría actual
function getCurrentCategory() {
  return document.querySelector(".category-filter.active")?.dataset.category || "all"
}

// Establecer categoría actual
function setCurrentCategory(category) {
  document.querySelectorAll(".category-filter").forEach((link) => {
    link.classList.remove("active")
  })
  document.querySelector(`[data-category="${category}"]`)?.classList.add("active")
}

// Abrir modal de producto
function openProductModal(product) {
  currentProduct = product

  // Llenar información del producto
  document.getElementById("modalProductImage").src = product.image
  document.getElementById("modalProductImage").alt = product.name
  document.getElementById("modalProductName").textContent = product.fullName
  document.getElementById("modalProductCategory").textContent = getCategoryName(product.category)

  const priceHTML = product.hasDiscount
    ? `S/. ${product.price.toFixed(2)} <small class="text-muted text-decoration-line-through">S/. ${product.originalPrice.toFixed(2)}</small>`
    : `S/. ${product.price.toFixed(2)}`
  document.getElementById("modalProductPrice").innerHTML = priceHTML

  document.getElementById("modalProductDescription").textContent = product.description

  // Llenar información adicional
  const infoList = document.getElementById("modalProductInfo")
  infoList.innerHTML = ""
  product.info.forEach((info) => {
    const li = document.createElement("li")
    li.textContent = info
    infoList.appendChild(li)
  })

  // Cargar comentarios
  loadComments(product)

  // Mostrar modal
  const modal = new bootstrap.Modal(document.getElementById("productModal"))
  modal.show()
}

// Configurar eventos del modal
function setupModalEvents() {
  // Controles de cantidad
  document.getElementById("decreaseQty").addEventListener("click", () => {
    const qtyInput = document.getElementById("quantity")
    const currentQty = Number.parseInt(qtyInput.value)
    if (currentQty > 1) {
      qtyInput.value = currentQty - 1
    }
  })

  document.getElementById("increaseQty").addEventListener("click", () => {
    const qtyInput = document.getElementById("quantity")
    const currentQty = Number.parseInt(qtyInput.value)
    qtyInput.value = currentQty + 1
  })

  // Sistema de calificación
  document.querySelectorAll(".star").forEach((star) => {
    star.addEventListener("click", function () {
      const rating = Number.parseInt(this.dataset.rating)
      document.getElementById("selectedRating").value = rating
      updateStarDisplay(rating)
    })

    star.addEventListener("mouseenter", function () {
      const rating = Number.parseInt(this.dataset.rating)
      updateStarDisplay(rating)
    })
  })

  document.querySelector(".rating-stars").addEventListener("mouseleave", () => {
    const selectedRating = Number.parseInt(document.getElementById("selectedRating").value)
    updateStarDisplay(selectedRating)
  })

  // Agregar comentario
  document.getElementById("addComment").addEventListener("click", addComment)

  // Agregar al carrito desde modal
  document.getElementById("addToCart").addEventListener("click", () => {
    if (currentProduct) {
      const quantity = Number.parseInt(document.getElementById("quantity").value)
      addToCart(currentProduct, quantity)
    }
  })
}

// Actualizar display de estrellas
function updateStarDisplay(rating) {
  document.querySelectorAll(".star").forEach((star, index) => {
    if (index < rating) {
      star.classList.add("active")
    } else {
      star.classList.remove("active")
    }
  })
}

// Cargar comentarios
function loadComments(product) {
  const commentsList = document.getElementById("commentsList")
  commentsList.innerHTML = ""

  if (product.comments.length === 0) {
    commentsList.innerHTML = '<p class="text-muted">No hay comentarios aún. ¡Sé el primero en comentar!</p>'
    return
  }

  product.comments.forEach((comment) => {
    const commentDiv = document.createElement("div")
    commentDiv.className = "comment-item"

    const stars = "★".repeat(comment.rating) + "☆".repeat(5 - comment.rating)

    commentDiv.innerHTML = `
            <div class="comment-rating">${stars}</div>
            <div class="d-flex justify-content-between">
                <span class="comment-author">${comment.author}</span>
                <span class="comment-date">${formatDate(comment.date)}</span>
            </div>
            <div class="comment-text">${comment.text}</div>
        `

    commentsList.appendChild(commentDiv)
  })
}

// Agregar comentario
function addComment() {
  const author = document.getElementById("commentAuthor").value.trim()
  const text = document.getElementById("commentText").value.trim()
  const rating = Number.parseInt(document.getElementById("selectedRating").value)

  if (!author || !text || rating === 0) {
    alert("Por favor completa todos los campos y selecciona una calificación.")
    return
  }

  const newComment = {
    author: author,
    rating: rating,
    text: text,
    date: new Date().toISOString().split("T")[0],
  }

  // Agregar comentario al producto
  currentProduct.comments.push(newComment)

  // Limpiar formulario
  document.getElementById("commentAuthor").value = ""
  document.getElementById("commentText").value = ""
  document.getElementById("selectedRating").value = "0"
  updateStarDisplay(0)

  // Recargar comentarios
  loadComments(currentProduct)
}

// Agregar al carrito
function addToCart(product, quantity = 1) {
  // Aquí implementarías la lógica del carrito
  console.log(`Agregado al carrito: ${product.name} x${quantity}`)

  // Mostrar notificación
  showNotification(`${product.name} agregado al carrito`)
}

// Mostrar notificación
function showNotification(message) {
  // Crear elemento de notificación
  const notification = document.createElement("div")
  notification.className = "alert alert-success position-fixed"
  notification.style.cssText = "top: 20px; right: 20px; z-index: 9999; min-width: 300px;"
  notification.innerHTML = `
        <i class="fas fa-check-circle me-2"></i>
        ${message}
    `

  document.body.appendChild(notification)

  // Remover después de 3 segundos
  setTimeout(() => {
    notification.remove()
  }, 3000)
}

// Obtener nombre de categoría
function getCategoryName(category) {
  const categoryNames = {
    medicamentos: "Medicamentos",
    vitaminas: "Vitaminas y Suplementos",
    "cuidado-cabello": "Cuidado del Cabello",
    bebes: "Bebés y Niños",
    fotoproteccion: "Fotoprotección",
    "cuidado-personal": "Cuidado Personal",
  }
  return categoryNames[category] || "General"
}

// Formatear fecha
function formatDate(dateString) {
  const date = new Date(dateString)
  return date.toLocaleDateString("es-ES", {
    year: "numeric",
    month: "long",
    day: "numeric",
  })
}

// Carousel functionality
let currentSlide = 0
const slides = document.querySelectorAll(".carousel-slide")
const indicators = document.querySelectorAll(".indicator")
const totalSlides = slides.length

// Productos del carrusel
const carouselProducts = [
  {
    id: 9,
    name: "NAN Sin Lactosa",
    fullName: "NAN Sin Lactosa - Fórmula Infantil",
    category: "bebes",
    price: 71.9,
    image: "/placeholder.svg?height=200&width=150",
    description:
      "Fórmula diseñada especialmente para el manejo dietario de una mala digestión/intolerancia a la lactosa.",
    isNew: false,
    hasDiscount: false,
    info: ["Presentación: Lata de 400g", "Edad: 0-12 meses", "Sin lactosa", "Fácil digestión"],
    comments: [],
  },
  // Los productos 4, 7, 2 ya están en la lista principal
]

// Agregar productos del carrusel a la lista principal si no existen
carouselProducts.forEach((product) => {
  if (!products.find((p) => p.id === product.id)) {
    products.push(product)
  }
})

// Función para mostrar slide
function showSlide(index) {
  // Ocultar todos los slides
  slides.forEach((slide) => slide.classList.remove("active"))
  indicators.forEach((indicator) => indicator.classList.remove("active"))

  // Mostrar slide actual
  slides[index].classList.add("active")
  indicators[index].classList.add("active")

  currentSlide = index
}

// Función para siguiente slide
function nextSlide() {
  const next = (currentSlide + 1) % totalSlides
  showSlide(next)
}

// Función para slide anterior
function prevSlide() {
  const prev = (currentSlide - 1 + totalSlides) % totalSlides
  showSlide(prev)
}

// Event listeners para navegación
document.getElementById("nextSlide").addEventListener("click", nextSlide)
document.getElementById("prevSlide").addEventListener("click", prevSlide)

// Event listeners para indicadores
indicators.forEach((indicator, index) => {
  indicator.addEventListener("click", () => showSlide(index))
})

// Event listeners para botones de compra del carrusel
document.querySelectorAll(".buy-btn").forEach((button) => {
  button.addEventListener("click", function () {
    const productId = Number.parseInt(this.dataset.productId)
    const product = products.find((p) => p.id === productId)

    if (product) {
      openProductModal(product)
    }
  })
})

// Auto-play del carrusel (opcional)
let autoPlayInterval = setInterval(nextSlide, 5000)

// Pausar auto-play al hacer hover
document.querySelector(".hero-banner").addEventListener("mouseenter", () => {
  clearInterval(autoPlayInterval)
})

// Reanudar auto-play al salir del hover
document.querySelector(".hero-banner").addEventListener("mouseleave", () => {
  autoPlayInterval = setInterval(nextSlide, 5000)
})

// Navegación con teclado
document.addEventListener("keydown", (e) => {
  if (e.key === "ArrowLeft") {
    prevSlide()
  } else if (e.key === "ArrowRight") {
    nextSlide()
  }
})

// Función para generar SQL INSERT (solo para desarrollo)
function generateSQLInserts() {
  console.log("-- SQL para insertar productos:")

  products.forEach((product) => {
    const info = JSON.stringify(product.info).replace(/'/g, "\\'")
    const description = product.description.replace(/'/g, "\\'")

    console.log(`
INSERT INTO productos (name, full_name, category, price, ${product.originalPrice ? "original_price, " : ""}image, description, is_new, has_discount, availability, info) 
VALUES ('${product.name}', '${product.fullName}', '${product.category}', ${product.price}, ${product.originalPrice || "NULL"}, '${product.image}', '${description}', ${product.isNew}, ${product.hasDiscount}, '${product.availability}', '${info}');`)
  })

  // Generar comentarios
  console.log("\n-- SQL para insertar comentarios:")
  products.forEach((product) => {
    if (product.comments && product.comments.length > 0) {
      product.comments.forEach((comment) => {
        console.log(`
INSERT INTO comentarios (producto_id, author, rating, text, date) 
VALUES (${product.id}, '${comment.author}', ${comment.rating}, '${comment.text.replace(/'/g, "\\'")}', '${comment.date}');`)
      })
    }
  })
}

// Llamar esta función en la consola del navegador para obtener los SQL
// generateSQLInserts();
