document.addEventListener("DOMContentLoaded", () => {
  // Elementos del DOM
  const clickableCards = document.querySelectorAll(".clickable-card")

  // Configurar tarjetas clickeables
  function setupClickableCards() {
    clickableCards.forEach((card) => {
      card.addEventListener("click", () => {
        const action = card.dataset.action
        const filter = card.dataset.filter

        if (action) {
          // Navegar a la página correspondiente
          if (action === "pedidos") {
            let url = "repartidor-pedidos.html"
            if (filter) {
              url += `?filter=${filter}`
            }
            window.location.href = url
          } else if (action === "historial") {
            let url = "repartidor-historial.html"
            if (filter) {
              url += `?filter=${filter}`
            }
            window.location.href = url
          } else if (action === "perfil") {
            window.location.href = "repartidor-perfil.html"
          }
        }
      })
    })
  }

  // Inicializar solo las tarjetas clickeables
  setupClickableCards()
})
