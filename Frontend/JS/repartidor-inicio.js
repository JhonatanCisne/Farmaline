const API_BASE_URL = 'http://localhost:8080/api';

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

async function updateRepartidorDashboard() {
    const repartidorData = getRepartidorData();
    const repartidorNombreSpan = document.getElementById('repartidor-nombre');
    const totalPedidosPendientesSpan = document.getElementById('total-pedidos');
    const tablaProximasEntregas = document.getElementById('tabla-proximas-entregas');

    if (!repartidorData) {
        console.warn("No se encontró sesión de repartidor. Redirigiendo a login.");
        localStorage.clear();
        window.location.href = "Login.html";
        return;
    }

    if (repartidorNombreSpan) {
        repartidorNombreSpan.textContent = repartidorData.nombre || 'Repartidor';
    }

    let todosLosPedidosDelRepartidor = [];
    try {
        if (!repartidorData.idRepartidor) {
            console.error("ID de repartidor no encontrado en los datos de usuario.");
            return;
        }
        const response = await fetch(`${API_BASE_URL}/pedidos/repartidor/${repartidorData.idRepartidor}`);
        if (!response.ok) {
            if (response.status === 204) {
                console.warn("No hay pedidos asignados a este repartidor.");
                todosLosPedidosDelRepartidor = [];
            } else {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        } else {
            todosLosPedidosDelRepartidor = await response.json();
            console.log("Pedidos obtenidos:", todosLosPedidosDelRepartidor);
        }
    } catch (error) {
        console.error('Error al cargar todos los pedidos del repartidor:', error);
        alert('Error al cargar los pedidos. Por favor, intente de nuevo.');
        todosLosPedidosDelRepartidor = [];
    }

    const pedidosPendientesDV = todosLosPedidosDelRepartidor.filter(pedido =>
        pedido.estadoRepartidorVerificacion === 'Pendiente'
    );
    if (totalPedidosPendientesSpan) {
        totalPedidosPendientesSpan.textContent = pedidosPendientesDV.length;
    }

    const proximasEntregas = todosLosPedidosDelRepartidor;

    if (tablaProximasEntregas) {
        if (proximasEntregas.length > 0) {
            tablaProximasEntregas.innerHTML = proximasEntregas.map(pedido => `
                <tr>
                    <td>#${pedido.idPedido}</td>
                    <td>${pedido.nombreUsuario || 'N/A'}</td>
                    <td>${pedido.domicilioUsuario || 'N/A'}</td>
                    <td>${pedido.hora || 'N/A'}</td>
                    <td><span class="badge bg-info">${pedido.estadoPedido || 'Desconocido'}</span></td>
                    <td>
                        ${pedido.estadoRepartidorVerificacion === 'Pendiente' ?
                            `<button class="btn btn-sm btn-success btn-confirmar-entrega" data-pedido-id="${pedido.idPedido}">
                                Confirmar Entrega
                            </button>` :
                            `<span class="badge bg-success">Entrega Confirmada</span>`
                        }
                    </td>
                </tr>
            `).join('');
            document.querySelectorAll('.btn-confirmar-entrega').forEach(button => {
                button.addEventListener('click', handleConfirmarEntrega);
            });
        } else {
            tablaProximasEntregas.innerHTML = `<tr><td colspan="6" class="text-center py-4">No hay entregas programadas</td></tr>`;
        }
    }
}

async function handleConfirmarEntrega(event) {
    const pedidoId = event.target.dataset.pedidoId;
    if (!pedidoId) {
        alert("Error: ID de pedido no encontrado.");
        return;
    }

    const confirmacion = confirm("¿Estás seguro de que deseas confirmar la recepción del pago para este pedido?");
    if (!confirmacion) {
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
            alert("Pago y entrega confirmados con éxito por el repartidor.");
            updateRepartidorDashboard();
        } else {
            const errorData = await response.text();
            alert(`Error al confirmar la entrega: ${errorData || response.statusText}`);
            console.error('Error al confirmar la entrega:', response.status, errorData);
        }
    } catch (error) {
        console.error('Error de red al confirmar la entrega:', error);
        alert("Error de red al intentar confirmar la entrega.");
    }
}

document.addEventListener('DOMContentLoaded', () => {
    updateRepartidorDashboard();

    const logoutSidebarBtn = document.getElementById('logout');
    if (logoutSidebarBtn) {
        logoutSidebarBtn.addEventListener('click', (event) => {
            event.preventDefault();
            localStorage.clear();
            window.location.href = "Login.html";
        });
    }

    const logoutHeaderBtn = document.getElementById('logout-header');
    if (logoutHeaderBtn) {
        logoutHeaderBtn.addEventListener('click', (event) => {
            event.preventDefault();
            localStorage.clear();
            window.location.href = "Login.html";
        });
    }

    const toggleSidebarBtn = document.getElementById('toggle-sidebar-btn');
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.querySelector('.main-content');

    if (toggleSidebarBtn && sidebar && mainContent) {
        toggleSidebarBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('sidebar-collapsed');
        });
    }

    document.querySelectorAll('.clickable-card').forEach(card => {
        card.addEventListener('click', function() {
            const action = this.dataset.action;
            const filter = this.dataset.filter;
            if (action === 'pedidos') {
                alert(`Funcionalidad para redirigir a pedidos filtrados: ${filter}`);
            }
        });
    });
});