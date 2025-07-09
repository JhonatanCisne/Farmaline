document.addEventListener('DOMContentLoaded', () => {
    const ordersContainer = document.getElementById('ordersContainer');
    const noOrdersMessage = document.getElementById('noOrdersMessage');
    const refreshOrdersBtn = document.getElementById('refreshOrdersBtn');

    const API_BASE_URL = 'http://localhost:8080/api';
    const USER_ID_STORAGE_KEY = 'farmalineUserId';

    function getUserId() {
        const userId = localStorage.getItem(USER_ID_STORAGE_KEY);
        if (!userId) {
            console.error('No se pudo obtener el ID del usuario del almacenamiento local. Por favor, inicie sesión.');
            return null;
        }
        return parseInt(userId);
    }

    const userId = getUserId();

    if (!userId) {
        ordersContainer.innerHTML = `<div class="alert alert-info text-center" role="alert">
                                        Para ver tus pedidos, por favor <a href="Login.html">inicia sesión</a>.
                                    </div>`;
        noOrdersMessage.style.display = 'none';
        return;
    }

    refreshOrdersBtn.addEventListener('click', fetchAndRenderOrders);

    async function fetchAndRenderOrders() {
        ordersContainer.innerHTML = '';
        noOrdersMessage.style.display = 'none';

        try {
            const pedidosResponse = await fetch(`${API_BASE_URL}/pedidos/usuario/${userId}`);
            if (!pedidosResponse.ok) {
                if (pedidosResponse.status === 404) {
                    console.warn(`No se encontraron pedidos para el usuario con ID: ${userId}`);
                    noOrdersMessage.style.display = 'block';
                    return;
                }
                throw new Error(`Error al obtener pedidos: ${pedidosResponse.statusText}`);
            }
            const pedidos = await pedidosResponse.json();

            if (pedidos.length === 0) {
                noOrdersMessage.style.display = 'block';
                return;
            }

            pedidos.sort((a, b) => {
                const statusOrder = { 'Pendiente': 1, 'Procesando': 2, 'Enviado': 3, 'Entregado': 4, 'Cancelado': 5 };
                return statusOrder[a.estado] - statusOrder[b.estado];
            });

            for (const pedido of pedidos) {
                renderPedidoCard(pedido);
            }

        } catch (error) {
            console.error('Error al cargar los pedidos:', error);
            ordersContainer.innerHTML = `<div class="alert alert-danger" role="alert">
                                            Error al cargar los pedidos. Por favor, intente de nuevo.
                                        </div>`;
        }
    }

    function renderPedidoCard(pedido) {
        const orderCard = document.createElement('div');
        orderCard.classList.add('card', 'mb-4');
        orderCard.dataset.pedidoId = pedido.idPedido;

        let estadoBadgeClass = '';
        switch (pedido.estado) {
            case 'Pendiente':
                estadoBadgeClass = 'bg-warning';
                break;
            case 'Procesando':
                estadoBadgeClass = 'bg-info';
                break;
            case 'Enviado':
                estadoBadgeClass = 'bg-primary';
                break;
            case 'Entregado':
                estadoBadgeClass = 'bg-success';
                break;
            case 'Cancelado':
                estadoBadgeClass = 'bg-danger';
                break;
            default:
                estadoBadgeClass = 'bg-secondary';
        }

        const estadoUsuarioDV = pedido.estadoUsuarioVerificacion || 'N/A';
        const estadoRepartidorDV = pedido.estadoRepartidorVerificacion || 'N/A';

        let estadoUsuarioDVBadgeClass = '';
        switch (estadoUsuarioDV) {
            case 'PENDIENTE':
                estadoUsuarioDVBadgeClass = 'bg-warning';
                break;
            case 'Confirmado': // Aquí usamos 'Confirmado'
                estadoUsuarioDVBadgeClass = 'bg-success';
                break;
            default:
                estadoUsuarioDVBadgeClass = 'bg-secondary';
        }

        let estadoRepartidorDVBadgeClass = '';
        switch (estadoRepartidorDV) {
            case 'PENDIENTE':
                estadoRepartidorDVBadgeClass = 'bg-warning';
                break;
            case 'VERIFICADO': // Mantén 'VERIFICADO' para el repartidor si es así
                estadoRepartidorDVBadgeClass = 'bg-success';
                break;
            default:
                estadoRepartidorDVBadgeClass = 'bg-secondary';
        }

        // --- CAMBIO CLAVE EN EL RENDERIZADO DEL BOTÓN ---
        // El botón solo se muestra si el estadoUsuarioVerificacion NO es 'Confirmado'
        const showConfirmButton = pedido.estadoUsuarioVerificacion !== 'Confirmado'; 
        
        let confirmButtonHtml = '';
        let confirmMessageHtml = '';

        if (showConfirmButton) {
            confirmButtonHtml = `
                <button class="btn btn-teal btn-sm confirm-reception-btn" data-pedido-id="${pedido.idPedido}">
                    <i class="fas fa-check-circle me-1"></i> Confirmar Recepción
                </button>`;
        } else {
            // Si el botón no se muestra, significa que el estadoUsuarioVerificacion ya es 'Confirmado'.
            confirmMessageHtml = `
                <div class="alert alert-success py-2 px-3 mt-3 d-flex align-items-center justify-content-center" role="alert">
                    <i class="fas fa-check-double me-2"></i> Pedido Confirmado por Usuario
                </div>`;
        }
        // --- FIN CAMBIO CLAVE ---

        orderCard.innerHTML = `
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Pedido #${pedido.idPedido}</h5>
                <span class="badge ${estadoBadgeClass}">${pedido.estado || 'Desconocido'}</span>
            </div>
            <div class="card-body">
                <p><strong>Fecha de Pedido:</strong> ${new Date(pedido.fecha).toLocaleDateString()} ${pedido.hora}</p>
                <p><strong>Total:</strong> S/. ${pedido.montoTotalPedido.toFixed(2)}</p>
                <h6>Productos:</h6>
                <ul class="list-group list-group-flush mb-3">
                    ${pedido.detallesPedido.map(item => `
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <span>${item.cantidad} x ${item.nombreProducto || `Producto ID: ${item.idProducto}`}</span>
                            <span>S/. ${item.subtotalDetalle.toFixed(2)}</span>
                        </li>
                    `).join('')}
                </ul>
                <div class="mt-3">
                    <h6>Doble Verificación:</h6>
                    <p>
                        <strong>Estado Usuario:</strong> <span class="badge ${estadoUsuarioDVBadgeClass}">${estadoUsuarioDV}</span>
                    </p>
                    <p>
                        <strong>Estado Repartidor:</strong> <span class="badge ${estadoRepartidorDVBadgeClass}">${estadoRepartidorDV}</span>
                    </p>
                    ${confirmButtonHtml}
                    ${confirmMessageHtml}
                </div>
            </div>
        `;
        ordersContainer.appendChild(orderCard);

        const buttonElement = orderCard.querySelector('.confirm-reception-btn');
        if (buttonElement) { 
            buttonElement.addEventListener('click', handleConfirmReception);
        }
    }

    async function handleConfirmReception(event) {
        const confirmButton = event.target.closest('.confirm-reception-btn');

        if (!confirmButton) {
            console.error('Botón de confirmación no encontrado. Esto no debería ocurrir.');
            return;
        }

        const pedidoId = confirmButton.dataset.pedidoId;

        if (!pedidoId) {
            console.error('ID de pedido no encontrado en el botón para la confirmación.');
            return;
        }

        const confirmation = confirm("¿Estás seguro de que quieres confirmar la recepción de este pedido? Esta acción no se puede deshacer.");

        if (!confirmation) {
            return;
        }

        confirmButton.disabled = true;
        confirmButton.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Confirmando...`;

        try {
            const response = await fetch(`${API_BASE_URL}/pedidos/${pedidoId}/confirmar-entrega`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                alert('¡Recepción del pedido confirmada con éxito! ✅');
                
                const buttonContainer = confirmButton.parentElement;
                
                if (buttonContainer) {
                    confirmButton.remove();

                    const confirmedMessage = document.createElement('div');
                    confirmedMessage.classList.add('alert', 'alert-success', 'py-2', 'px-3', 'mt-3', 'd-flex', 'align-items-center', 'justify-content-center');
                    confirmedMessage.setAttribute('role', 'alert');
                    confirmedMessage.innerHTML = `<i class="fas fa-check-double me-2"></i> Pedido Confirmado por Usuario`;
                    
                    buttonContainer.appendChild(confirmedMessage);
                }

                // Esto recargará los pedidos. Asegúrate de que tu backend envíe
                // estadoUsuarioVerificacion: "Confirmado" después de tu acción PUT.
                fetchAndRenderOrders(); 

            } else {
                confirmButton.disabled = false;
                confirmButton.innerHTML = `<i class="fas fa-check-circle me-1"></i> Confirmar Recepción`;

                const errorText = await response.text();
                alert(`Error al confirmar la recepción: ${errorText} ⚠️`);
                console.error('Error al confirmar la recepción:', response.status, errorText);
            }
        } catch (error) {
            confirmButton.disabled = false;
            confirmButton.innerHTML = `<i class="fas fa-check-circle me-1"></i> Confirmar Recepción`;

            console.error('Error de red al confirmar la recepción:', error);
            alert('Error de red al intentar confirmar la recepción. 🌐');
        }
    }

    fetchAndRenderOrders();

    const logoutItem = document.getElementById('navLogoutItem');
    const accountLink = document.getElementById('navAccountLink');
    const userRole = localStorage.getItem('userRole');

    if (userRole) {
        if (logoutItem) logoutItem.style.display = 'block';
        if (accountLink) accountLink.style.display = 'none';

        const logoutBtn = document.getElementById('logoutButton');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (event) => {
                event.preventDefault();
                if (window.farmalineAuth && typeof window.farmalineAuth.logout === 'function') {
                    window.farmalineAuth.logout();
                } else {
                    localStorage.removeItem("farmalineUserId");
                    localStorage.removeItem("farmalineAdminId");
                    localStorage.removeItem("farmalineRepartidorId");
                    localStorage.removeItem("userRole");
                    window.location.href = "Login.html";
                }
            });
        }
    } else {
        if (logoutItem) logoutItem.style.display = 'none';
        if (accountLink) accountLink.style.display = 'block';
    }
});