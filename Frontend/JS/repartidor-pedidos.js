document.addEventListener("DOMContentLoaded", () => {
  // Elementos del DOM
  const tablaPedidos = document.getElementById("tabla-pedidos")
  const buscarPedido = document.getElementById("buscar-pedido")
  const filtroZona = document.getElementById("filtro-zona")
  const filtroUrgencia = document.getElementById("filtro-urgencia")

  // Configurar modal de confirmación
  const tipoConfirmacion = document.getElementById("tipo-confirmacion")
  const divNombreReceptor = document.getElementById("div-nombre-receptor")

  // Importar Bootstrap Modal
  const bootstrap = window.bootstrap

  if (tipoConfirmacion && divNombreReceptor) {
    tipoConfirmacion.addEventListener("change", function () {
      divNombreReceptor.style.display = this.value !== "cliente" ? "block" : "none"
    })
  }

  // Funciones globales
  window.actualizarPedidos = () => {
    console.log("Actualizando lista de pedidos...")
    mostrarNotificacion("Lista de pedidos actualizada", "info")
  }

  window.filtrarPedidos = (filtro) => {
    console.log(`Filtrando pedidos: ${filtro}`)

    // Actualizar botones activos
    document.querySelectorAll(".btn-group .btn").forEach((btn) => {
      btn.classList.remove("active")
    })
    event.target.classList.add("active")
  }

  window.verDetallesPedido = (pedidoId) => {
    document.getElementById("detalle-pedido-id").textContent = pedidoId

    // Mostrar modal
    const modal = new bootstrap.Modal(document.getElementById("modalDetallesPedido"))
    modal.show()
  }

  window.iniciarEntrega = (pedidoId) => {
    alert(`Iniciando entrega del pedido #${pedidoId}`)

    // Cerrar modal si está abierto
    const modalInstance = bootstrap.Modal.getInstance(document.getElementById("modalDetallesPedido"))
    if (modalInstance) {
      modalInstance.hide()
    }

    window.actualizarPedidos()
  }

  window.confirmarEntrega = (pedidoId) => {
    document.getElementById("confirmar-pedido-id").textContent = pedidoId

    // Mostrar modal
    const modal = new bootstrap.Modal(document.getElementById("modalConfirmarEntrega"))
    modal.show()
  }

  window.finalizarEntrega = () => {
    const pedidoId = document.getElementById("confirmar-pedido-id").textContent
    const tipoConfirmacion = document.getElementById("tipo-confirmacion").value
    const nombreReceptor = document.getElementById("nombre-receptor").value
    const observaciones = document.getElementById("observaciones-entrega").value
    const confirmaPago = document.getElementById("confirmar-pago").checked

    console.log(`Finalizando entrega del pedido #${pedidoId}`)

    // Cerrar modal
    const modalInstance = bootstrap.Modal.getInstance(document.getElementById("modalConfirmarEntrega"))
    modalInstance.hide()

    // Mostrar notificación
    mostrarNotificacion(`Pedido #${pedidoId} entregado correctamente`, "success")

    window.actualizarPedidos()
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
