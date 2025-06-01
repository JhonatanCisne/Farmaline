// Funcionalidad de búsqueda en el glosario
document.addEventListener("DOMContentLoaded", () => {
  // Configurar búsqueda
  setupGlosarioSearch()

  // Configurar navegación por letras
  setupLetterNavigation()

  // Configurar filtros
  setupFilters()

  // Agregar estilos CSS dinámicamente
  setTimeout(() => {
    addDynamicStyles()
  }, 100)
})

// Configurar funcionalidad de búsqueda
function setupGlosarioSearch() {
  const searchInput = document.getElementById("searchGlosario")

  if (searchInput) {
    searchInput.addEventListener("input", function () {
      const searchTerm = this.value.toLowerCase()
      searchInGlosario(searchTerm)
    })
  }
}

// Función principal de búsqueda
function searchInGlosario(searchTerm) {
  const cards = document.querySelectorAll(".card")
  let hasResults = false

  // Si no hay término de búsqueda, aplicar filtros actuales
  if (!searchTerm.trim()) {
    applyCurrentFilters()
    clearHighlights()
    return
  }

  // Buscar en las tarjetas
  cards.forEach((card) => {
    const title = card.querySelector(".card-title").textContent.toLowerCase()
    const content = card.querySelector(".card-text").textContent.toLowerCase()

    if (title.includes(searchTerm) || content.includes(searchTerm)) {
      // Verificar si también pasa los filtros actuales
      if (passesCurrentFilters(card)) {
        card.parentElement.style.display = "block"
        highlightText(card, searchTerm)
        hasResults = true
      } else {
        card.parentElement.style.display = "none"
      }
    } else {
      card.parentElement.style.display = "none"
    }
  })

  // Actualizar visibilidad de accordions y expandir los que tienen resultados
  updateAccordionVisibility(true)

  // Mostrar mensaje si no hay resultados
  showNoResultsMessage(!hasResults, searchTerm)
}

// Verificar si una tarjeta pasa los filtros actuales
function passesCurrentFilters(card) {
  const medicationType = document.getElementById("medicationType")
  const availability = document.getElementById("availability")

  if (!medicationType || !availability) return true

  const content = card.querySelector(".card-text").textContent.toLowerCase()
  const title = card.querySelector(".card-title").textContent.toLowerCase()
  const fullText = (title + " " + content).toLowerCase()

  // Verificar filtro de tipo
  const selectedType = medicationType.value.toLowerCase()
  if (selectedType) {
    let typeMatch = false

    switch (selectedType) {
      case "analgésico":
      case "analgesico":
        typeMatch =
          fullText.includes("analgésico") ||
          fullText.includes("analgesico") ||
          fullText.includes("dolor") ||
          fullText.includes("paracetamol") ||
          fullText.includes("metamizol") ||
          fullText.includes("dipirona")
        break
      case "antibiótico":
      case "antibiotico":
        typeMatch =
          fullText.includes("antibiótico") ||
          fullText.includes("antibiotico") ||
          fullText.includes("amoxicilina") ||
          fullText.includes("cefalexina") ||
          fullText.includes("ciprofloxacino") ||
          fullText.includes("penicilina") ||
          fullText.includes("cefalosporina") ||
          fullText.includes("fluoroquinolona")
        break
      case "antiinflamatorio":
        typeMatch =
          fullText.includes("antiinflamatorio") ||
          fullText.includes("aine") ||
          fullText.includes("ibuprofeno") ||
          fullText.includes("diclofenaco") ||
          fullText.includes("inflamación") ||
          fullText.includes("aspirina")
        break
      case "antidiabético":
      case "antidiabetico":
        typeMatch =
          fullText.includes("antidiabético") ||
          fullText.includes("antidiabetico") ||
          fullText.includes("diabetes") ||
          fullText.includes("metformina") ||
          fullText.includes("insulina") ||
          fullText.includes("glucemia")
        break
      case "antihipertensivo":
        typeMatch =
          fullText.includes("antihipertensivo") ||
          fullText.includes("hipertensión") ||
          fullText.includes("hipertension") ||
          fullText.includes("losartán") ||
          fullText.includes("losartan") ||
          fullText.includes("presión arterial")
        break
      case "antihistamínico":
      case "antihistaminico":
        typeMatch =
          fullText.includes("antihistamínico") ||
          fullText.includes("antihistaminico") ||
          fullText.includes("alergia") ||
          fullText.includes("loratadina") ||
          fullText.includes("rinitis") ||
          fullText.includes("urticaria")
        break
      case "corticosteroide":
        typeMatch =
          fullText.includes("corticosteroide") ||
          fullText.includes("glucocorticoide") ||
          fullText.includes("prednisona") ||
          fullText.includes("dexametasona") ||
          fullText.includes("cortisona")
        break
      case "broncodilatador":
        typeMatch =
          fullText.includes("broncodilatador") ||
          fullText.includes("asma") ||
          fullText.includes("salbutamol") ||
          fullText.includes("broncoespasmo") ||
          fullText.includes("epoc")
        break
      case "inhibidor":
        typeMatch =
          fullText.includes("inhibidor") ||
          fullText.includes("omeprazol") ||
          fullText.includes("bomba de protones") ||
          fullText.includes("simvastatina") ||
          fullText.includes("estatina")
        break
      case "hormona":
        typeMatch =
          fullText.includes("hormona") ||
          fullText.includes("insulina") ||
          fullText.includes("oxitocina") ||
          fullText.includes("endocrina")
        break
      default:
        typeMatch = fullText.includes(selectedType)
    }

    if (!typeMatch) return false
  }

  // Verificar filtro de disponibilidad
  const selectedAvailability = availability.value
  if (selectedAvailability === "libre") {
    if (!fullText.includes("venta libre") && !fullText.includes("disponibilidad:** venta libre")) return false
  }
  if (selectedAvailability === "receta") {
    if (
      !fullText.includes("requiere receta") &&
      !fullText.includes("receta médica") &&
      !fullText.includes("disponibilidad:** requiere receta")
    )
      return false
  }

  return true
}

// Aplicar filtros actuales sin búsqueda
function applyCurrentFilters() {
  const cards = document.querySelectorAll(".card")
  let hasResults = false

  cards.forEach((card) => {
    if (passesCurrentFilters(card)) {
      card.parentElement.style.display = "block"
      hasResults = true
    } else {
      card.parentElement.style.display = "none"
    }
  })

  // Actualizar visibilidad de accordions y expandir los que tienen resultados
  updateAccordionVisibility(true)

  // Mostrar mensaje si no hay resultados
  showNoResultsMessage(!hasResults)
}

// Resaltar texto de búsqueda
function highlightText(card, searchTerm) {
  if (!searchTerm) return

  const title = card.querySelector(".card-title")
  const content = card.querySelector(".card-text")

  // Limpiar highlights anteriores
  clearHighlights(card)

  // Resaltar en título
  if (title) {
    const titleText = title.textContent
    const highlightedTitle = highlightTextInString(titleText, searchTerm)
    if (highlightedTitle !== titleText) {
      title.innerHTML = title.innerHTML.replace(titleText, highlightedTitle)
    }
  }

  // Resaltar en contenido
  if (content) {
    const contentText = content.textContent
    const highlightedContent = highlightTextInString(contentText, searchTerm)
    if (highlightedContent !== contentText) {
      content.innerHTML = content.innerHTML.replace(contentText, highlightedContent)
    }
  }
}

// Función auxiliar para resaltar texto en string
function highlightTextInString(text, searchTerm) {
  if (!searchTerm) return text
  const regex = new RegExp(`(${escapeRegExp(searchTerm)})`, "gi")
  return text.replace(regex, '<mark class="search-highlight">$1</mark>')
}

// Escapar caracteres especiales para regex
function escapeRegExp(string) {
  return string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")
}

// Limpiar highlights
function clearHighlights(container = document) {
  const highlights = container.querySelectorAll(".search-highlight")
  highlights.forEach((highlight) => {
    const parent = highlight.parentNode
    parent.replaceChild(document.createTextNode(highlight.textContent), highlight)
    parent.normalize()
  })
}

// Mostrar mensaje de "no hay resultados"
function showNoResultsMessage(show, searchTerm = "") {
  let noResultsDiv = document.getElementById("noResultsMessage")

  if (show) {
    if (!noResultsDiv) {
      noResultsDiv = document.createElement("div")
      noResultsDiv.id = "noResultsMessage"
      noResultsDiv.className = "alert alert-info text-center mt-4"

      const accordion = document.getElementById("glosarioAccordion")
      accordion.parentNode.insertBefore(noResultsDiv, accordion.nextSibling)
    }

    const message = searchTerm
      ? `No se encontraron medicamentos que coincidan con "<strong>${searchTerm}</strong>"`
      : "No se encontraron medicamentos con los filtros seleccionados"

    noResultsDiv.innerHTML = `
      <i class="fas fa-search fa-2x mb-3 text-muted"></i>
      <h5>No se encontraron resultados</h5>
      <p class="mb-0">${message}</p>
      <small class="text-muted">Intenta con otros términos o filtros</small>
    `
    noResultsDiv.style.display = "block"
  } else {
    if (noResultsDiv) {
      noResultsDiv.style.display = "none"
    }
  }
}

// Configurar navegación por letras
function setupLetterNavigation() {
  // Crear navegación alfabética
  createAlphabetNavigation()

  // Configurar clicks en letras
  document.querySelectorAll(".letter-nav").forEach((letter) => {
    letter.addEventListener("click", function (e) {
      e.preventDefault()
      const targetLetter = this.dataset.letter
      navigateToLetter(targetLetter)
    })
  })
}

// Crear navegación alfabética
function createAlphabetNavigation() {
  const searchContainer = document.querySelector(".row.mb-4")
  if (!searchContainer) return

  const alphabetNav = document.createElement("div")
  alphabetNav.className = "col-12 mt-3"
  alphabetNav.innerHTML = `
    <div class="text-center">
      <div class="btn-group flex-wrap" role="group" aria-label="Navegación alfabética">
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="A">A</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="C">C</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="D">D</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="I">I</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="L">L</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="M">M</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="O">O</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="P">P</button>
        <button type="button" class="btn btn-outline-teal btn-sm letter-nav" data-letter="S">S</button>
      </div>
    </div>
  `

  searchContainer.appendChild(alphabetNav)
}

// Navegar a una letra específica
function navigateToLetter(letter) {
  const targetAccordion = document.getElementById(`collapse${letter}`)
  if (targetAccordion) {
    // Cerrar otros accordions
    document.querySelectorAll(".accordion-collapse.show").forEach((collapse) => {
      if (collapse.id !== `collapse${letter}`) {
        const button = document.querySelector(`[data-bs-target="#${collapse.id}"]`)
        if (button) {
          button.click()
        }
      }
    })

    // Abrir el accordion objetivo
    if (!targetAccordion.classList.contains("show")) {
      const button = document.querySelector(`[data-bs-target="#collapse${letter}"]`)
      if (button) {
        button.click()
      }
    }

    // Scroll suave al elemento
    setTimeout(() => {
      targetAccordion.scrollIntoView({
        behavior: "smooth",
        block: "start",
      })
    }, 300)

    // Resaltar temporalmente
    highlightSection(targetAccordion)
  }
}

// Resaltar sección temporalmente
function highlightSection(element) {
  element.style.backgroundColor = "rgba(32, 178, 170, 0.1)"
  element.style.transition = "background-color 0.3s ease"

  setTimeout(() => {
    element.style.backgroundColor = ""
  }, 2000)
}

// Configurar filtros adicionales
function setupFilters() {
  // Crear filtros
  createMedicationTypeFilter()
  createAvailabilityFilter()
  addClearFiltersButton()
}

// Crear filtro por tipo de medicamento
function createMedicationTypeFilter() {
  const searchContainer = document.querySelector(".row.mb-4")
  if (!searchContainer) return

  const filterContainer = document.createElement("div")
  filterContainer.className = "col-md-6 mt-3"
  filterContainer.innerHTML = `
    <label for="medicationType" class="form-label">
      <i class="fas fa-filter me-1"></i>
      Filtrar por tipo:
    </label>
    <select class="form-select" id="medicationType">
      <option value="">Todos los tipos</option>
      <option value="analgésico">Analgésicos</option>
      <option value="antibiótico">Antibióticos</option>
      <option value="antiinflamatorio">Antiinflamatorios</option>
      <option value="antidiabético">Antidiabéticos</option>
      <option value="antihipertensivo">Antihipertensivos</option>
      <option value="antihistamínico">Antihistamínicos</option>
      <option value="corticosteroide">Corticosteroides</option>
      <option value="broncodilatador">Broncodilatadores</option>
      <option value="inhibidor">Inhibidores</option>
      <option value="hormona">Hormonas</option>
    </select>
  `

  searchContainer.appendChild(filterContainer)

  // Event listener para el filtro
  document.getElementById("medicationType").addEventListener("change", () => {
    applyFilters()
  })
}

// Crear filtro por disponibilidad
function createAvailabilityFilter() {
  const searchContainer = document.querySelector(".row.mb-4")
  if (!searchContainer) return

  const filterContainer = document.createElement("div")
  filterContainer.className = "col-md-6 mt-3"
  filterContainer.innerHTML = `
    <label for="availability" class="form-label">
      <i class="fas fa-prescription-bottle me-1"></i>
      Disponibilidad:
    </label>
    <select class="form-select" id="availability">
      <option value="">Todos</option>
      <option value="libre">Venta libre</option>
      <option value="receta">Requiere receta</option>
    </select>
  `

  searchContainer.appendChild(filterContainer)

  // Event listener para el filtro
  document.getElementById("availability").addEventListener("change", () => {
    applyFilters()
  })
}

// Aplicar todos los filtros
function applyFilters() {
  const searchInput = document.getElementById("searchGlosario")
  const searchTerm = searchInput ? searchInput.value.toLowerCase() : ""

  if (searchTerm) {
    // Si hay búsqueda, usar la función de búsqueda que ya incluye filtros
    searchInGlosario(searchTerm)
  } else {
    // Si no hay búsqueda, solo aplicar filtros
    applyCurrentFilters()
  }
}

// Actualizar visibilidad de accordions
function updateAccordionVisibility(expandWithResults = false) {
  const accordionItems = document.querySelectorAll(".accordion-item")

  accordionItems.forEach((item) => {
    const visibleCards = item.querySelectorAll('.card:not([style*="display: none"])')

    if (visibleCards.length > 0) {
      item.style.display = "block"

      // Si hay resultados y se solicita expansión automática
      if (expandWithResults) {
        const collapseElement = item.querySelector(".accordion-collapse")
        if (collapseElement && !collapseElement.classList.contains("show")) {
          const button = item.querySelector(".accordion-button")
          if (button) {
            button.click()
          }
        }
      }
    } else {
      item.style.display = "none"
    }
  })
}

// Función para limpiar todos los filtros
function clearAllFilters() {
  // Limpiar búsqueda
  const searchInput = document.getElementById("searchGlosario")
  if (searchInput) {
    searchInput.value = ""
  }

  // Limpiar filtros
  const medicationType = document.getElementById("medicationType")
  const availability = document.getElementById("availability")

  if (medicationType) medicationType.value = ""
  if (availability) availability.value = ""

  // Mostrar todas las tarjetas
  const cards = document.querySelectorAll(".card")
  cards.forEach((card) => {
    card.parentElement.style.display = "block"
  })

  // Mostrar todos los accordions
  const accordionItems = document.querySelectorAll(".accordion-item")
  accordionItems.forEach((item) => {
    item.style.display = "block"
  })

  // Limpiar highlights
  clearHighlights()

  // Ocultar mensaje de no resultados
  showNoResultsMessage(false)
}

// Agregar botón para limpiar filtros
function addClearFiltersButton() {
  const searchContainer = document.querySelector(".row.mb-4")
  if (!searchContainer) return

  const clearButton = document.createElement("div")
  clearButton.className = "col-12 mt-3 text-center"
  clearButton.innerHTML = `
    <button type="button" class="btn btn-outline-secondary btn-sm" id="clearFilters">
      <i class="fas fa-times me-1"></i>
      Limpiar filtros
    </button>
  `

  searchContainer.appendChild(clearButton)

  // Event listener para limpiar filtros
  document.getElementById("clearFilters").addEventListener("click", clearAllFilters)
}

// Agregar estilos CSS dinámicamente
function addDynamicStyles() {
  const style = document.createElement("style")
  style.textContent = `
    .search-highlight {
      background-color: #fff3cd;
      color: #856404;
      padding: 1px 2px;
      border-radius: 2px;
    }
    
    .btn-outline-teal {
      color: #20b2aa;
      border-color: #20b2aa;
    }
    
    .btn-outline-teal:hover {
      color: white;
      background-color: #20b2aa;
      border-color: #20b2aa;
    }
    
    .letter-nav {
      margin: 2px;
    }
    
    .accordion-item {
      transition: all 0.3s ease;
    }
    
    .form-select:focus {
      border-color: #20b2aa;
      box-shadow: 0 0 0 0.2rem rgba(32, 178, 170, 0.25);
    }
    
    #clearFilters:hover {
      background-color: #6c757d;
      border-color: #6c757d;
      color: white;
    }
  `

  document.head.appendChild(style)
}
