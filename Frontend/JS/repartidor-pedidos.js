document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = 'http://localhost:8080/api';

    const tablaPedidos = document.getElementById("tabla-pedidos");
    const buscarPedido = document.getElementById("buscar-pedido");
    const filtroZona = document.getElementById("filtro-zona");
    const filtroUrgencia = document.getElementById("filtro-urgencia");

    const tipoConfirmacion = document.getElementById("tipo-confirmacion");
    const divNombreReceptor = document.getElementById("div-nombre-receptor");
    const nombreReceptorInput = document.getElementById("nombre-receptor");

    const bootstrap = window.bootstrap;

    let todosLosPedidos = [];
    let pedidosFiltrados = [];
    let repartidorId = null;

    function getRepartidorData() {
        const userDataString = localStorage.getItem("userData");
        const userRole = localStorage.getItem("userRole");

        if (userDataString && userRole === 'repartidor') {
            try {
                return JSON.parse(userDataString);
            } catch (e) {
                console.error("Error parsing repartidor data from localStorage", e);
                return null;
            }
        }
        return null;
    }

    function mostrarNotificacion(mensaje, tipo = "info") {
        const notification = document.createElement("div");
        notification.className = `alert alert-${tipo} position-fixed`;
        notification.style.cssText = "top: 20px; right: 20px; z-index: 9999; min-width: 300px;";

        const iconos = {
            success: "check-circle",
            danger: "exclamation-circle",
            warning: "exclamation-triangle",
            info: "info-circle",
        };

        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="fas fa-${iconos[tipo]} me-2"></i>
                <div>${mensaje}</div>
                <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
        `;
        document.body.appendChild(notification);
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 4000);
    }

    async function cargarPedidos(tipoCarga = 'noAsignados') {
        const repartidorData = getRepartidorData();
        if (!repartidorData) {
            console.warn("No se encontró sesión de repartidor. Redirigiendo a login.");
            localStorage.clear();
            window.location.href = "Login.html";
            return;
        }
        repartidorId = repartidorData.idRepartidor;

        let url = '';
        if (tipoCarga === 'noAsignados') {
            url = `${API_BASE_URL}/pedidos/no-asignados`;
        } else if (tipoCarga === 'misPedidos') {
            url = `${API_BASE_URL}/pedidos/repartidor/${repartidorId}`;
        } else {
            console.error("Tipo de carga de pedidos desconocido:", tipoCarga);
            return;
        }

        try {
            const response = await fetch(url);
            if (!response.ok) {
                if (response.status === 204) {
                    todosLosPedidos = [];
                    mostrarNotificacion(`No hay ${tipoCarga === 'noAsignados' ? 'pedidos disponibles para asignar' : 'pedidos asignados a ti'}.`, 'info');
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } else {
                todosLosPedidos = await response.json();
                console.log(`Pedidos (${tipoCarga}):`, todosLosPedidos);
            }
            aplicarFiltros();
        } catch (error) {
            console.error('Error al cargar los pedidos:', error);
            mostrarNotificacion('Error al cargar los pedidos. Por favor, intente de nuevo.', 'danger');
            todosLosPedidos = [];
            aplicarFiltros();
        }
    }

    function aplicarFiltros() {
        let tempPedidos = [...todosLosPedidos];

        const searchTerm = buscarPedido.value.toLowerCase().trim();
        if (searchTerm) {
            tempPedidos = tempPedidos.filter(pedido =>
                (pedido.idPedido && pedido.idPedido.toString().includes(searchTerm)) ||
                (pedido.nombreUsuario && pedido.nombreUsuario.toLowerCase().includes(searchTerm)) ||
                (pedido.domicilioUsuario && pedido.domicilioUsuario.toLowerCase().includes(searchTerm)) ||
                (pedido.estadoPedido && pedido.estadoPedido.toLowerCase().includes(searchTerm))
            );
        }

        const selectedZona = filtroZona.value;
        if (selectedZona) {
            tempPedidos = tempPedidos.filter(pedido =>
                pedido.zonaEntrega && pedido.zonaEntrega.toLowerCase() === selectedZona
            );
        }

        const selectedUrgencia = filtroUrgencia.value;
        if (selectedUrgencia) {
            tempPedidos = tempPedidos.filter(pedido =>
                pedido.tipoEntrega && pedido.tipoEntrega.toLowerCase() === selectedUrgencia
            );
        }

        pedidosFiltrados = tempPedidos;
        renderizarTablaPedidos(pedidosFiltrados);
    }

    function renderizarTablaPedidos(pedidos) {
        if (!tablaPedidos) {
            console.error("Elemento 'tabla-pedidos' no encontrado.");
            return;
        }

        if (pedidos.length === 0) {
            tablaPedidos.innerHTML = `<tr><td colspan="7" class="text-center py-4">No hay pedidos disponibles con los filtros actuales.</td></tr>`;
            return;
        }

        tablaPedidos.innerHTML = pedidos.map(pedido => {
            const esPedidoAsignado = pedido.idRepartidor === repartidorId;
            const botonAccion = esPedidoAsignado ?
                (pedido.estadoRepartidorVerificacion === 'Pendiente' ?
                    `<button class="btn btn-sm btn-success btn-confirmar-entrega" data-pedido-id="${pedido.idPedido}">Confirmar Entrega</button>` :
                    `<span class="badge bg-success">Entrega Confirmada</span>`
                ) :
                `<button class="btn btn-sm btn-primary btn-asignar-pedido" data-pedido-id="${pedido.idPedido}">Asignarme</button>`;

            const productosHtml = pedido.detallesPedido && pedido.detallesPedido.length > 0
                ? pedido.detallesPedido.map(p => `${p.nombreProducto} (x${p.cantidad})`).join('<br>')
                : 'N/A';

            return `
                <tr>
                    <td>#${pedido.idPedido}</td>
                    <td>${pedido.nombreUsuario || 'N/A'}</td>
                    <td>${pedido.domicilioUsuario || 'N/A'}</td>
                    <td>${productosHtml}</td>
                    <td>${pedido.hora || 'N/A'}</td>
                    <td><span class="badge bg-info">${pedido.estadoPedido || 'Desconocido'}</span></td>
                    <td>
                        <button class="btn btn-sm btn-info me-2 btn-ver-detalles" data-pedido-id="${pedido.idPedido}">
                            <i class="fas fa-eye"></i>
                        </button>
                        ${botonAccion}
                    </td>
                </tr>
            `;
        }).join('');

        document.querySelectorAll('.btn-ver-detalles').forEach(button => {
            button.addEventListener('click', (event) => verDetallesPedido(event.currentTarget.dataset.pedidoId));
        });

        document.querySelectorAll('.btn-asignar-pedido').forEach(button => {
            button.addEventListener('click', (event) => asignarPedido(event.currentTarget.dataset.pedidoId));
        });

        document.querySelectorAll('.btn-confirmar-entrega').forEach(button => {
            button.addEventListener('click', (event) => confirmarEntrega(event.currentTarget.dataset.pedidoId));
        });
    }

    async function asignarPedido(pedidoId) {
        const confirmacion = confirm(`¿Estás seguro de que quieres asignarte el pedido #${pedidoId}?`);
        if (!confirmacion) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/pedidos/${pedidoId}/asignar-repartidor/${repartidorId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                mostrarNotificacion(`Pedido #${pedidoId} asignado con éxito a tu cuenta.`, 'success');
                cargarPedidos('noAsignados');
            } else {
                const errorData = await response.json();
                mostrarNotificacion(`Error al asignar pedido: ${errorData.message || response.statusText}`, 'danger');
                console.error('Error al asignar pedido:', response.status, errorData);
            }
        } catch (error) {
            console.error('Error de red al asignar el pedido:', error);
            mostrarNotificacion("Error de red al intentar asignar el pedido.", 'danger');
        }
    }

    window.actualizarPedidos = () => {
        mostrarNotificacion("Lista de pedidos actualizada", "info");
        cargarPedidos('noAsignados');
    };

    window.filtrarPedidos = (filtro) => {
        console.log(`Filtrando pedidos: ${filtro}`);

        document.querySelectorAll(".btn-group .btn").forEach((btn) => {
            btn.classList.remove("active");
        });
        
        let tempPedidos = [];
        if (filtro === 'todos') {
            cargarPedidos('noAsignados'); 
        } else if (filtro === 'misPedidos') {
            cargarPedidos('misPedidos');
        } 
    };

    window.verDetallesPedido = (pedidoId) => {
        const pedido = todosLosPedidos.find(p => p.idPedido.toString() === pedidoId.toString());
        if (!pedido) {
            console.error("Pedido no encontrado para detalles:", pedidoId);
            mostrarNotificacion("Detalles del pedido no disponibles.", 'danger');
            return;
        }

        document.getElementById("detalle-pedido-id").textContent = pedido.idPedido || 'N/A';
        document.getElementById("detalle-cliente-nombre").textContent = pedido.nombreUsuario || 'N/A';
        document.getElementById("detalle-cliente-telefono").textContent = pedido.telefonoUsuario || 'N/A';
        document.getElementById("detalle-cliente-email").textContent = pedido.correoUsuario || 'N/A';
        document.getElementById("detalle-direccion").textContent = pedido.domicilioUsuario || 'N/A';
        document.getElementById("detalle-referencia").textContent = pedido.referenciaDomicilio || 'N/A';
        document.getElementById("detalle-hora").textContent = pedido.hora || 'N/A';

        const detalleProductosBody = document.getElementById("detalle-productos");
        if (pedido.detallesPedido && pedido.detallesPedido.length > 0) {
            detalleProductosBody.innerHTML = pedido.detallesPedido.map(item => `
                <tr>
                    <td>${item.nombreProducto}</td>
                    <td>${item.cantidad}</td>
                    <td>S/. ${item.precioUnitarioAlMomentoCompra ? item.precioUnitarioAlMomentoCompra.toFixed(2) : '0.00'}</td>
                    <td>S/. ${item.subtotalDetalle ? item.subtotalDetalle.toFixed(2) : '0.00'}</td>
                </tr>
            `).join('');
        } else {
            detalleProductosBody.innerHTML = `<tr><td colspan="4" class="text-center">No hay productos en este pedido.</td></tr>`;
        }

        document.getElementById("detalle-total").textContent = `S/. ${pedido.montoTotalPedido ? pedido.montoTotalPedido.toFixed(2) : '0.00'}`;

        const modal = new bootstrap.Modal(document.getElementById("modalDetallesPedido"));
        modal.show();
    };

    window.confirmarEntrega = (pedidoId) => {
        document.getElementById("confirmar-pedido-id").textContent = pedidoId;
        tipoConfirmacion.value = 'cliente';
        divNombreReceptor.style.display = 'none';
        nombreReceptorInput.value = '';
        document.getElementById('observaciones-entrega').value = '';
        document.getElementById('confirmar-pago').checked = true;

        const modal = new bootstrap.Modal(document.getElementById("modalConfirmarEntrega"));
        modal.show();
    };

    window.finalizarEntrega = async () => {
        const pedidoId = document.getElementById("confirmar-pedido-id").textContent;
        const tipoConfirmacionValue = tipoConfirmacion.value;
        const nombreReceptorValue = nombreReceptorInput.value;
        const observaciones = document.getElementById("observaciones-entrega").value;
        const confirmaPago = document.getElementById("confirmar-pago").checked;

        if (tipoConfirmacionValue !== 'cliente' && !nombreReceptorValue.trim()) {
            mostrarNotificacion("Por favor, ingresa el nombre de quien recibe.", 'warning');
            return;
        }

        if (!confirmaPago) {
            mostrarNotificacion("Debes confirmar el pago para finalizar la entrega.", 'warning');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/pedidos/${pedidoId}/confirmar-pago`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                mostrarNotificacion(`Pedido #${pedidoId} entregado y pago confirmado con éxito.`, "success");
                const modalInstance = bootstrap.Modal.getInstance(document.getElementById("modalConfirmarEntrega"));
                if (modalInstance) modalInstance.hide();
                cargarPedidos('misPedidos');
            } else {
                const errorData = await response.json();
                mostrarNotificacion(`Error al confirmar entrega: ${errorData.message || response.statusText}`, "danger");
                console.error('Error al confirmar entrega:', response.status, errorData);
            }
        } catch (error) {
            console.error('Error de red al finalizar la entrega:', error);
            mostrarNotificacion("Error de red al intentar finalizar la entrega.", 'danger');
        }
    };

    if (tipoConfirmacion && divNombreReceptor) {
        tipoConfirmacion.addEventListener("change", function () {
            divNombreReceptor.style.display = this.value !== "cliente" ? "block" : "none";
            if (this.value === 'cliente') {
                nombreReceptorInput.value = '';
            }
        });
    }

    buscarPedido.addEventListener('input', aplicarFiltros);
    filtroZona.addEventListener('change', aplicarFiltros);
    filtroUrgencia.addEventListener('change', aplicarFiltros);

    const logoutSidebarBtn = document.getElementById('logout');
    if (logoutSidebarBtn) {
        logoutSidebarBtn.addEventListener('click', (event) => {
            event.preventDefault();
            localStorage.clear();
            window.location.href = "Login.html";
        });
    }

    cargarPedidos('noAsignados');
});