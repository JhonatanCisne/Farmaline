document.addEventListener('DOMContentLoaded', () => {
    let allPedidos = [];
    let pedidoDetailsModal;

    if (document.getElementById('pedidoDetailsModal')) {
        pedidoDetailsModal = new bootstrap.Modal(document.getElementById('pedidoDetailsModal'));
    }

    const loadPedidos = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/pedidos'); 
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            allPedidos = await response.json();
            displayPedidos(allPedidos);
        } catch (error) {
            console.error('Error fetching pedidos:', error);
            const tablaPedidosBody = document.querySelector('#tablaPedidos tbody');
            tablaPedidosBody.innerHTML = `<tr><td colspan="7">Error al cargar los pedidos. Por favor, verifica la consola del navegador y el log del servidor.</td></tr>`;
        }
    };

    const displayPedidos = (pedidosToDisplay) => {
        const tablaPedidosBody = document.querySelector('#tablaPedidos tbody');
        tablaPedidosBody.innerHTML = '';

        if (pedidosToDisplay.length === 0) {
            tablaPedidosBody.innerHTML = `<tr><td colspan="7">No hay pedidos que coincidan con los filtros.</td></tr>`;
            return;
        }

        pedidosToDisplay.forEach(pedido => {
            const row = tablaPedidosBody.insertRow();
            const estadoBadge = getEstadoBadge(pedido.estado);
            const repartidorNombre = pedido.idRepartidor ? 'Asignado' : 'No Asignado'; 

            row.innerHTML = `
                <td>${pedido.idPedido}</td>
                <td>${pedido.nombreUsuario}</td>
                <td>${repartidorNombre}</td>
                <td>${pedido.fecha}</td>
                <td>S/ ${pedido.montoTotalPedido ? pedido.montoTotalPedido.toFixed(2) : 'N/A'}</td> 
                <td>${estadoBadge}</td>
                <td>
                    <button class="btn btn-sm btn-info view-details-btn" data-id="${pedido.idPedido}" title="Ver Detalles"><i class="fas fa-eye"></i></button>
                </td>
            `;
        });

        document.querySelectorAll('.view-details-btn').forEach(button => {
            button.addEventListener('click', (event) => {
                const pedidoId = event.currentTarget.dataset.id;
                showPedidoDetailsModal(parseInt(pedidoId));
            });
        });
    };

    const getEstadoBadge = (estado) => {
        switch (estado) {
            case 'Pendiente':
                return '<span class="badge bg-warning text-dark">Pendiente</span>';
            case 'Completado':
                return '<span class="badge bg-success">Completado</span>';
            case 'Cancelado':
                return '<span class="badge bg-danger">Cancelado</span>';
            case 'En Camino':
                return '<span class="badge bg-primary">En Camino</span>';
            case 'Confirmado':
                 return '<span class="badge bg-info">Confirmado</span>';
            default:
                return `<span class="badge bg-secondary">${estado}</span>`;
        }
    };

    const applyFilters = () => {
        const filtroUsuario = document.getElementById('filtroUsuario').value.toLowerCase();
        const filtroRepartidor = document.getElementById('filtroRepartidor').value.toLowerCase();
        const filtroEstado = document.getElementById('filtroEstado').value.toLowerCase();

        const filteredPedidos = allPedidos.filter(pedido => {
            const matchesUsuario = pedido.nombreUsuario && pedido.nombreUsuario.toLowerCase().includes(filtroUsuario);
            const repartidorText = pedido.idRepartidor ? 'asignado' : 'no asignado';
            const matchesRepartidor = repartidorText.includes(filtroRepartidor);
            const matchesEstado = filtroEstado === '' || (pedido.estado && pedido.estado.toLowerCase() === filtroEstado);

            return matchesUsuario && matchesRepartidor && matchesEstado;
        });

        displayPedidos(filteredPedidos);
    };

    const showPedidoDetailsModal = (idPedido) => {
        const pedido = allPedidos.find(p => p.idPedido === idPedido);

        if (!pedido) {
            console.error('Pedido no encontrado para ID:', idPedido);
            return;
        }

        document.getElementById('modalPedidoId').textContent = pedido.idPedido;
        document.getElementById('modalUsuarioNombre').textContent = pedido.nombreUsuario + ' ' + (pedido.apellidoUsuario || ''); 
        // Asumiendo que `nombreRepartidor` viene en el DTO o se mapea
        document.getElementById('modalRepartidorNombre').textContent = pedido.nombreRepartidor || 'No Asignado'; 
        document.getElementById('modalFechaPedido').textContent = pedido.fecha || 'N/A';
        document.getElementById('modalMontoTotal').textContent = pedido.montoTotalPedido ? pedido.montoTotalPedido.toFixed(2) : '0.00';
        document.getElementById('modalEstadoPedido').innerHTML = getEstadoBadge(pedido.estado);

        const modalProductosBody = document.getElementById('modalProductosBody');
        modalProductosBody.innerHTML = '';
        let subtotalProductos = 0;

        if (pedido.detallesPedido && pedido.detallesPedido.length > 0) { 
            pedido.detallesPedido.forEach(detalle => {
                const row = modalProductosBody.insertRow();
                // Usamos directamente subtotalDetalle del JSON
                const subtotalItem = detalle.subtotalDetalle || 0; 
                subtotalProductos += subtotalItem;
                row.innerHTML = `
                    <td>${detalle.nombreProducto || 'N/A'}</td>
                    <td>${detalle.cantidad || 0}</td>
                    <td>S/ ${subtotalItem.toFixed(2)}</td>
                `;
            });
        } else {
            modalProductosBody.innerHTML = `<tr><td colspan="3">No hay productos en este pedido.</td></tr>`;
        }
        document.getElementById('modalSubtotalProductos').textContent = `S/ ${subtotalProductos.toFixed(2)}`;

        if (pedidoDetailsModal) {
            pedidoDetailsModal.show();
        }
    };

    document.getElementById('filtroUsuario').addEventListener('input', applyFilters);
    document.getElementById('filtroRepartidor').addEventListener('input', applyFilters);
    document.getElementById('filtroEstado').addEventListener('change', applyFilters);

    loadPedidos();
});