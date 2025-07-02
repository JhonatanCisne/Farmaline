document.addEventListener("DOMContentLoaded", () => {
  // Elementos del DOM
  const tablaHistorial = document.getElementById("tabla-historial")
  const fechaDesde = document.getElementById("fecha-desde")
  const fechaHasta = document.getElementById("fecha-hasta")

  // Establecer fechas por defecto
  const hoy = new Date()
  const hace7Dias = new Date(hoy.getTime() - 7 * 24 * 60 * 60 * 1000)

  if (fechaHasta) {
    fechaHasta.value = hoy.toISOString().split("T")[0]
  }
  if (fechaDesde) {
    fechaDesde.value = hace7Dias.toISOString().split("T")[0]
  }

  // Función global para filtrar por fecha
  window.filtrarPorFecha = () => {
    const desde = fechaDesde.value
    const hasta = fechaHasta.value

    console.log(`Filtrando por fecha: ${desde} hasta ${hasta}`)

    // Mostrar notificación
    mostrarNotificacion(`Mostrando entregas del ${desde} al ${hasta}`, "info")
  }

  // Función para mostrar notificaciones
  function mostrarNotificacion(mensaje, tipo = "info") {
    const notification = document.createElement("div")
    notification.className = `alert alert-${tipo} position-fixed`
    notification.style.cssText = "top: 20px; right: 20px; z-index: 9999; min-width: 300px;"

    const iconos = {
      success: "check-circle",
      danger: "exclamation-circle",
      warning: "exclamation-triangle",
      info: "info-circle",
    }

    notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="fas fa-${iconos[tipo]} me-2"></i>
                <div>${mensaje}</div>
                <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
        `

    document.body.appendChild(notification)

    // Auto-remove después de 4 segundos
    setTimeout(() => {
      if (notification.parentNode) {
        notification.remove()
      }
    }, 4000)
  }
})
