class AdminPanel {
    constructor() {
        this.API_BASE_URL = 'http://localhost:8080';
        this.productos = [];
        this.repartidores = [];
        this.vehiculos = []; // Nueva propiedad para almacenar vehículos
        this.currentSection = 'inicio';
        this.currentStockFilter = '';
        this.initEventListeners();
        this.render();
    }

    initEventListeners() {
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.addEventListener('click', (event) => {
                event.preventDefault();
                const section = event.currentTarget.dataset.section;
                this.changeSection(section);
            });
        });

        document.getElementById('toggle-sidebar').addEventListener('click', () => {
            this.toggleSidebar();
        });

        document.getElementById('logout').addEventListener('click', () => {
            this.logout();
        });

        document.getElementById('logout-header').addEventListener('click', () => {
            this.logout();
        });

        document.querySelectorAll('.clickable-card').forEach(card => {
            card.addEventListener('click', (event) => {
                const action = event.currentTarget.dataset.action;
                const filter = event.currentTarget.dataset.filter;
                if (action === 'productos') {
                    this.currentStockFilter = filter;
                    if (filter === 'critico') {
                        document.getElementById('filter-indicator').innerHTML = '<span class="badge bg-danger ms-2">Stock Crítico</span>';
                    } else if (filter === 'bajo') {
                        document.getElementById('filter-indicator').innerHTML = '<span class="badge bg-warning text-dark ms-2">Stock Bajo</span>';
                    } else {
                        document.getElementById('filter-indicator').innerHTML = '';
                    }
                    this.changeSection('productos');
                } else if (action === 'repartidores') {
                    this.changeSection('repartidores');
                }
            });
        });

        document.getElementById('cantidad-agregar').addEventListener('input', () => {
            this.calcularStockNuevo();
        });

        const guardarProductoBtn = document.getElementById('guardar-producto-btn');
        if (guardarProductoBtn) {
            guardarProductoBtn.addEventListener('click', this.handleGuardarProductoClick.bind(this));
        }

        const guardarStockBtn = document.getElementById('guardar-stock-btn');
        if (guardarStockBtn) {
            guardarStockBtn.addEventListener('click', () => {
                this.confirmarActualizacionStock();
            });
        }

        const guardarRepartidorBtn = document.getElementById('guardar-repartidor-btn');
        if (guardarRepartidorBtn) {
            guardarRepartidorBtn.addEventListener('click', this.handleGuardarRepartidorClick.bind(this));
        }

        const productoPrecioInput = document.getElementById('producto-precio');
        const productoIgvInput = document.getElementById('producto-igv');
        const productoPrecioFinalInput = document.getElementById('producto-precio-final');

        if (productoPrecioInput && productoIgvInput && productoPrecioFinalInput) {
            const calculatePrecioFinal = () => {
                const precio = parseFloat(productoPrecioInput.value) || 0;
                const igv = parseFloat(productoIgvInput.value) || 0;
                const precioFinal = precio * (1 + igv / 100);
                productoPrecioFinalInput.value = precioFinal.toFixed(2);
            };

            productoPrecioInput.addEventListener('input', calculatePrecioFinal);
            productoIgvInput.addEventListener('input', calculatePrecioFinal);
        }

        const productoFechaIngresoInput = document.getElementById('producto-fecha-ingreso');
        if (productoFechaIngresoInput) {
            const today = new Date();
            const year = today.getFullYear();
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const day = String(today.getDate()).padStart(2, '0');
            productoFechaIngresoInput.value = `${year}-${month}-${day}`;
        }

        const newProductModal = document.getElementById('modalNuevoProducto');
        if (newProductModal) {
            newProductModal.addEventListener('hidden.bs.modal', () => {
                document.getElementById('modalNuevoProductoLabel').innerHTML = '<i class="fas fa-plus-circle"></i> Añadir Nuevo Producto';
                const btn = document.getElementById('guardar-producto-btn');
                btn.textContent = 'Guardar Producto';
                delete btn.dataset.editingId;
                document.getElementById('formNuevoProducto').reset();
                document.getElementById('producto-precio-final').value = '';
                const productoFechaIngresoInput = document.getElementById('producto-fecha-ingreso');
                if (productoFechaIngresoInput) {
                    const today = new Date();
                    const year = today.getFullYear();
                    const month = String(today.getMonth() + 1).padStart(2, '0');
                    const day = String(today.getDate()).padStart(2, '0');
                    productoFechaIngresoInput.value = `${year}-${month}-${day}`;
                }
            });
        }

        const repartidorModal = document.getElementById('modalRepartidor');
        if (repartidorModal) {
            repartidorModal.addEventListener('show.bs.modal', async () => { // Evento para cargar vehículos al abrir el modal
                await this.cargarVehiculosDesdeBackend();
                this.poblarSelectVehiculos();
            });
            repartidorModal.addEventListener('hidden.bs.modal', () => {
                document.getElementById('modalRepartidorLabel').innerHTML = '<i class="fas fa-user-plus"></i> Añadir Nuevo Repartidor';
                const btn = document.getElementById('guardar-repartidor-btn');
                btn.textContent = 'Guardar Repartidor';
                delete btn.dataset.editingId;
                document.getElementById('formRepartidor').reset();
                // Asegurarse de que el select de vehículos se resetee
                const vehiculoSelect = document.getElementById('repartidor-id-vehiculo');
                if (vehiculoSelect) {
                    vehiculoSelect.value = '';
                }
                // Restaurar el ID de administrador por defecto si existe el campo
                const adminIdInput = document.getElementById('repartidor-id-administrador');
                if (adminIdInput) {
                    adminIdInput.value = '1';
                }
            });
        }
    }

    handleGuardarProductoClick() {
        const guardarProductoBtn = document.getElementById('guardar-producto-btn');
        const editingId = guardarProductoBtn.dataset.editingId;
        if (editingId) {
            this.confirmarEdicionProducto(parseInt(editingId));
        } else {
            this.confirmarNuevoProducto();
        }
    }

    handleGuardarRepartidorClick() {
        const guardarRepartidorBtn = document.getElementById('guardar-repartidor-btn');
        const editingId = guardarRepartidorBtn.dataset.editingId;
        if (editingId) {
            this.confirmarEdicionRepartidor(parseInt(editingId));
        } else {
            this.confirmarNuevoRepartidor();
        }
    }

    async render() {
        await this.cargarProductosDesdeBackend();
        await this.cargarRepartidoresDesdeBackend();
        await this.cargarVehiculosDesdeBackend(); // Cargar vehículos al inicio
        this.actualizarDashboard();
        this.changeSection(this.currentSection);
    }

    async cargarProductosDesdeBackend() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/api/productos`);
            if (!response.ok) {
                if (response.status === 204) {
                    this.productos = [];
                    this.mostrarNotificacion('No se encontraron productos en el servidor.', 'info');
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } else {
                this.productos = await response.json();
            }
        } catch (error) {
            console.error('Error al cargar productos desde el backend:', error);
            this.mostrarNotificacion('Error al cargar productos. Intenta recargar la página.', 'danger');
            this.productos = [];
        }
        this.cargarTablaProductos();
        this.actualizarDashboard();
    }

    async cargarRepartidoresDesdeBackend() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/api/repartidores`);
            if (!response.ok) {
                if (response.status === 204) {
                    this.repartidores = [];
                    this.mostrarNotificacion('No se encontraron repartidores en el servidor.', 'info');
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } else {
                this.repartidores = await response.json();
            }
        } catch (error) {
            console.error('Error al cargar repartidores desde el backend:', error);
            this.mostrarNotificacion('Error al cargar repartidores. Intenta recargar la página.', 'danger');
            this.repartidores = [];
        }
        this.cargarTablaRepartidores();
        this.actualizarDashboard();
    }

    async cargarVehiculosDesdeBackend() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/api/vehiculos`); // Asume un endpoint /api/vehiculos
            if (!response.ok) {
                if (response.status === 204) {
                    this.vehiculos = [];
                    this.mostrarNotificacion('No se encontraron vehículos en el servidor.', 'info');
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } else {
                this.vehiculos = await response.json();
            }
            this.poblarSelectVehiculos(); // Poblar el select después de cargar
        } catch (error) {
            console.error('Error al cargar vehículos desde el backend:', error);
            this.mostrarNotificacion('Error al cargar vehículos. Intenta recargar la página o verifica la conexión con el servidor de vehículos.', 'danger');
            this.vehiculos = [];
        }
    }

    poblarSelectVehiculos() {
        const vehiculoSelect = document.getElementById('repartidor-id-vehiculo');
        if (vehiculoSelect) {
            vehiculoSelect.innerHTML = '<option value="">Selecciona un vehículo</option>'; // Limpiar y añadir opción por defecto
            this.vehiculos.forEach(vehiculo => {
                const option = document.createElement('option');
                option.value = vehiculo.idVehiculo;
                option.textContent = `${vehiculo.marca} - ${vehiculo.modelo} (${vehiculo.placa})`;
                vehiculoSelect.appendChild(option);
            });
        }
    }

    async confirmarActualizacionStock() {
        const productoId = parseInt(document.getElementById('producto-id-stock').value);
        const cantidadAgregar = parseInt(document.getElementById('cantidad-agregar').value);

        if (cantidadAgregar <= 0) {
            this.mostrarNotificacion('La cantidad a añadir debe ser mayor que cero.', 'warning');
            return;
        }

        try {
            const response = await fetch(`${this.API_BASE_URL}/api/productos/${productoId}/stock?cantidadCambio=${cantidadAgregar}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al actualizar stock: ${errorText || response.statusText}`);
            }

            const updatedProducto = await response.json();

            const index = this.productos.findIndex(p => p.idProducto === updatedProducto.idProducto);
            if (index !== -1) {
                this.productos[index].stockDisponible = updatedProducto.stockDisponible;
                this.productos[index].nombre = updatedProducto.nombre;
            }

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalStock'));
            modalInstance.hide();

            this.actualizarDashboard();
            this.cargarTablaProductos();

            this.mostrarNotificacion(`Stock de ${updatedProducto.nombre} actualizado a ${updatedProducto.stockDisponible}`, 'success');

        } catch (error) {
            console.error('Error al actualizar stock:', error);
            this.mostrarNotificacion(`Error al actualizar stock: ${error.message}`, 'danger');
        }
    }

    async confirmarNuevoProducto() {
        const nombre = document.getElementById('producto-nombre').value;
        const stockInicial = parseInt(document.getElementById('producto-stock-inicial').value);
        const descripcion = document.getElementById('producto-descripcion').value || "Sin descripción";
        const precio = parseFloat(document.getElementById('producto-precio').value) || 0.0;
        const igv = parseFloat(document.getElementById('producto-igv').value) || 0.0;
        const precioFinal = parseFloat(document.getElementById('producto-precio-final').value) || 0.0;
        const fechaCaducidadStr = document.getElementById('producto-fecha-caducidad').value || null;
        const fechaIngresoStr = document.getElementById('producto-fecha-ingreso').value;

        const imagenInput = document.getElementById('producto-imagen');
        const imagenFile = imagenInput.files[0];

        if (!nombre || isNaN(stockInicial) || stockInicial < 0 || isNaN(precio) || isNaN(igv) || isNaN(precioFinal) || !fechaIngresoStr) {
            this.mostrarNotificacion('Por favor, completa todos los campos del producto obligatorios (nombre, stock, precio, IGV, precio final, fecha de ingreso) correctamente.', 'warning');
            return;
        }

        if (!imagenFile) {
            this.mostrarNotificacion('Por favor, selecciona una imagen para el producto.', 'warning');
            return;
        }

        const newProductData = {
            nombre: nombre,
            stockDisponible: stockInicial,
            descripcion: descripcion,
            precio: precio,
            igv: igv,
            precioFinal: precioFinal,
            fechaCaducidad: fechaCaducidadStr,
            fechaIngreso: fechaIngresoStr
        };

        const formData = new FormData();
        formData.append('producto', new Blob([JSON.stringify(newProductData)], { type: 'application/json' }));
        formData.append('file', imagenFile);

        try {
            const response = await fetch(`${this.API_BASE_URL}/api/productos`, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`Error al crear producto: ${errorData.message}`);
            }

            const createdProducto = await response.json();
            this.productos.push(createdProducto);

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalNuevoProducto'));
            modalInstance.hide();

            document.getElementById('formNuevoProducto').reset();
            this.actualizarDashboard();
            this.cargarTablaProductos();

            this.mostrarNotificacion(`Producto "${createdProducto.nombre}" añadido con éxito (ID: ${createdProducto.idProducto})`, 'success');

        } catch (error) {
            console.error('Error al añadir nuevo producto:', error);
            this.mostrarNotificacion(`Error al añadir producto: ${error.message}`, 'danger');
        }
    }

    async confirmarEdicionProducto(productId) {
        const nombre = document.getElementById('producto-nombre').value;
        const stockInicial = parseInt(document.getElementById('producto-stock-inicial').value);
        const descripcion = document.getElementById('producto-descripcion').value || "Sin descripción";
        const precio = parseFloat(document.getElementById('producto-precio').value) || 0.0;
        const igv = parseFloat(document.getElementById('producto-igv').value) || 0.0;
        const precioFinal = parseFloat(document.getElementById('producto-precio-final').value) || 0.0;
        const fechaCaducidadStr = document.getElementById('producto-fecha-caducidad').value || null;
        const fechaIngresoStr = document.getElementById('producto-fecha-ingreso').value;

        const imagenInput = document.getElementById('producto-imagen');
        const imagenFile = imagenInput.files[0];

        if (!nombre || isNaN(stockInicial) || stockInicial < 0 || isNaN(precio) || isNaN(igv) || isNaN(precioFinal) || !fechaIngresoStr) {
            this.mostrarNotificacion('Por favor, completa todos los campos del producto obligatorios correctamente.', 'warning');
            return;
        }

        const updatedProductData = {
            idProducto: productId,
            nombre: nombre,
            stockDisponible: stockInicial,
            descripcion: descripcion,
            precio: precio,
            igv: igv,
            precioFinal: precioFinal,
            fechaCaducidad: fechaCaducidadStr,
            fechaIngreso: fechaIngresoStr
        };

        const formData = new FormData();
        formData.append('producto', new Blob([JSON.stringify(updatedProductData)], { type: 'application/json' }));
        if (imagenFile) {
            formData.append('file', imagenFile);
        }

        try {
            const response = await fetch(`${this.API_BASE_URL}/api/productos/${productId}`, {
                method: 'PUT',
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`Error al actualizar producto: ${errorData.message}`);
            }

            const updatedProducto = await response.json();
            const index = this.productos.findIndex(p => p.idProducto === updatedProducto.idProducto);
            if (index !== -1) {
                this.productos[index] = updatedProducto;
            }

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalNuevoProducto'));
            modalInstance.hide();

            this.actualizarDashboard();
            this.cargarTablaProductos();

            this.mostrarNotificacion(`Producto "${updatedProducto.nombre}" actualizado con éxito`, 'success');

        } catch (error) {
            console.error('Error al editar producto:', error);
            this.mostrarNotificacion(`Error al editar producto: ${error.message}`, 'danger');
        }
    }

    openEditProductModal(productId) {
        const product = this.productos.find(p => p.idProducto === productId);
        if (!product) {
            this.mostrarNotificacion('Producto no encontrado para editar.', 'danger');
            return;
        }

        document.getElementById('modalNuevoProductoLabel').innerHTML = '<i class="fas fa-edit"></i> Editar Producto';
        const guardarProductoBtn = document.getElementById('guardar-producto-btn');
        guardarProductoBtn.textContent = 'Guardar Cambios';
        guardarProductoBtn.dataset.editingId = product.idProducto;

        document.getElementById('producto-nombre').value = product.nombre;
        document.getElementById('producto-descripcion').value = product.descripcion || '';
        document.getElementById('producto-stock-inicial').value = product.stockDisponible;
        document.getElementById('producto-precio').value = product.precio;
        document.getElementById('producto-igv').value = product.igv;
        document.getElementById('producto-precio-final').value = product.precioFinal;
        document.getElementById('producto-fecha-caducidad').value = product.fechaCaducidad || '';
        document.getElementById('producto-fecha-ingreso').value = product.fechaIngreso || '';

        const modal = new bootstrap.Modal(document.getElementById('modalNuevoProducto'));
        modal.show();
    }

    async deleteProduct(productId) {
        if (!confirm('¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.')) {
            return;
        }
        try {
            const response = await fetch(`${this.API_BASE_URL}/api/productos/${productId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al eliminar producto: ${errorText || response.statusText}`);
            }

            this.productos = this.productos.filter(p => p.idProducto !== productId);
            this.actualizarDashboard();
            this.cargarTablaProductos();
            this.mostrarNotificacion('Producto eliminado con éxito.', 'success');
        } catch (error) {
            console.error('Error al eliminar producto:', error);
            this.mostrarNotificacion(`Error al eliminar producto: ${error.message}`, 'danger');
        }
    }

    changeSection(section) {
        this.currentSection = section;
        document.querySelectorAll('.content-section').forEach(sec => {
            sec.classList.remove('active');
        });
        document.getElementById(`${section}-section`).classList.add('active');

        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.classList.remove('active');
        });
        document.querySelector(`.sidebar-nav .nav-link[data-section="${section}"]`).classList.add('active');

        const pageTitle = document.getElementById('page-title');
        switch (section) {
            case 'inicio':
                pageTitle.textContent = 'Inicio';
                this.actualizarDashboard();
                break;
            case 'productos':
                pageTitle.textContent = 'Productos';
                this.currentCategory = '';
                const categoryButtons = document.querySelectorAll('.category-btn');
                if (categoryButtons.length > 0) {
                    categoryButtons.forEach(btn => btn.classList.remove('active'));
                    const allProductsButton = document.querySelector('[data-category=""]');
                    if (allProductsButton) {
                        allProductsButton.classList.add('active');
                    }
                }
                this.cargarTablaProductos();
                break;
            case 'repartidores':
                pageTitle.textContent = 'Repartidores';
                this.cargarTablaRepartidores();
                break;
        }

        if (section !== 'productos') {
            this.currentStockFilter = '';
            document.getElementById('filter-indicator').innerHTML = '';
        }
    }

    toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        const mainContent = document.getElementById('main-content');
        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('expanded');
    }

    actualizarDashboard() {
        const totalProductos = this.productos.length;
        const stockCritico = this.productos.filter(p => p.stockDisponible <= 10).length;
        const stockBajo = this.productos.filter(p => p.stockDisponible > 10 && p.stockDisponible <= 20).length;
        const totalRepartidores = this.repartidores.length;

        document.getElementById('total-productos').textContent = totalProductos;
        document.getElementById('stock-critico').textContent = stockCritico;
        document.getElementById('stock-bajo').textContent = stockBajo;
        document.getElementById('total-repartidores').textContent = totalRepartidores;
    }

    cargarTablaProductos() {
        const tbody = document.getElementById('tabla-productos');
        let productosFiltrados = this.productos;

        if (this.currentStockFilter === 'critico') {
            productosFiltrados = productosFiltrados.filter(p => p.stockDisponible <= 10);
        } else if (this.currentStockFilter === 'bajo') {
            productosFiltrados = productosFiltrados.filter(p => p.stockDisponible > 10 && p.stockDisponible <= 20);
        }

        tbody.innerHTML = productosFiltrados.map(p => `
            <tr>
                <td><strong>#${p.idProducto}</strong></td>
                <td>${p.nombre}</td>
                <td>
                    <span class="badge ${this.getStockBadgeClass(p.stockDisponible)}">${p.stockDisponible}</span>
                </td>
                <td>${p.fechaIngreso || 'N/A'}</td>
                <td>${p.fechaCaducidad || 'N/A'}</td>
                <td>
                    <button class="btn btn-sm btn-info text-white me-1" onclick="adminPanel.abrirModalStock(${p.idProducto})" title="Actualizar Stock">
                        <i class="fas fa-box"></i>
                    </button>
                    <button class="btn btn-sm btn-warning text-white me-1" onclick="adminPanel.openEditProductModal(${p.idProducto})" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="adminPanel.deleteProduct(${p.idProducto})" title="Eliminar">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');

        if (productosFiltrados.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">
                        <i class="fas fa-search fa-2x mb-2"></i><br>
                        No se encontraron productos con los filtros aplicados
                    </td>
                </tr>
            `;
        }
    }

    getStockBadgeClass(stockDisponible) {
        if (stockDisponible <= 10) return 'bg-danger';
        if (stockDisponible <= 20) return 'bg-warning text-dark';
        return 'bg-success'
    }

    cargarTablaRepartidores() {
        const tbody = document.getElementById('tabla-repartidores');

        if (this.repartidores.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="fas fa-truck fa-2x mb-2"></i><br>
                        No hay repartidores registrados
                    </td>
                </tr>
            `;
        } else {
            tbody.innerHTML = this.repartidores.map(r => `
                <tr>
                    <td><strong>#${r.idRepartidor}</strong></td>
                    <td>${r.nombre} ${r.apellido || ''}</td>
                    <td>${r.telefono}</td>
                    <td>${r.placaVehiculo || 'N/A'}</td>
                    <td>
                        <button class="btn btn-sm btn-warning text-white me-1" onclick="adminPanel.openEditRepartidorModal(${r.idRepartidor})" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="adminPanel.deleteRepartidor(${r.idRepartidor})" title="Eliminar">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    }

    abrirModalStock(productoId) {
        const producto = this.productos.find(p => p.idProducto === productoId);
        if (producto) {
            document.getElementById('producto-id-stock').value = producto.idProducto;
            document.getElementById('producto-nombre-stock').value = producto.nombre;
            document.getElementById('stock-actual').value = producto.stockDisponible;
            document.getElementById('cantidad-agregar').value = 10;
            this.calcularStockNuevo();

            const modal = new bootstrap.Modal(document.getElementById('modalStock'));
            modal.show();
        }
    }

    calcularStockNuevo() {
        const stockActual = parseInt(document.getElementById('stock-actual').value) || 0;
        const cantidadAgregar = parseInt(document.getElementById('cantidad-agregar').value) || 0;
        document.getElementById('stock-nuevo').value = stockActual + cantidadAgregar;
    }

    async confirmarNuevoRepartidor() {
        const nombre = document.getElementById('repartidor-nombre').value;
        const apellido = document.getElementById('repartidor-apellido') ? document.getElementById('repartidor-apellido').value : 'N/A';
        const telefono = document.getElementById('repartidor-telefono').value;
        const contrasena = document.getElementById('repartidor-contrasena') ? document.getElementById('repartidor-contrasena').value : 'password123';
        // Obtener el valor seleccionado del select
        const idVehiculo = document.getElementById('repartidor-id-vehiculo').value;
        const idVehiculoParsed = idVehiculo ? parseInt(idVehiculo) : null; // Convertir a número o null
        const idAdministrador = parseInt(document.getElementById('repartidor-id-administrador').value) || 1;


        if (!nombre || !telefono || !contrasena || idVehiculoParsed === null || isNaN(idAdministrador) || idAdministrador < 1) {
            this.mostrarNotificacion('Por favor, completa todos los campos obligatorios para el repartidor (Nombre, Teléfono, Contraseña, Vehículo Asignado, ID Administrador) correctamente.', 'warning');
            return;
        }

        const nuevoRepartidorData = {
            nombre: nombre,
            apellido: apellido,
            telefono: telefono,
            contrasena: contrasena,
            idVehiculo: idVehiculoParsed, // Usar el ID del vehículo seleccionado
            idAdministrador: idAdministrador
        };

        try {
            const response = await fetch(`${this.API_BASE_URL}/api/repartidores`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(nuevoRepartidorData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`Error al crear repartidor: ${errorData.message}`);
            }

            const createdRepartidor = await response.json();
            this.repartidores.push(createdRepartidor);

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalRepartidor'));
            modalInstance.hide();

            document.getElementById('formRepartidor').reset();
            this.actualizarDashboard();
            this.cargarTablaRepartidores();

            this.mostrarNotificacion(`Repartidor ${createdRepartidor.nombre} ${createdRepartidor.apellido} creado con éxito`, 'success');

        } catch (error) {
            console.error('Error al añadir nuevo repartidor:', error);
            this.mostrarNotificacion(`Error al añadir repartidor: ${error.message}`, 'danger');
        }
    }

    openEditRepartidorModal(repartidorId) {
        const repartidor = this.repartidores.find(r => r.idRepartidor === repartidorId);
        if (!repartidor) {
            this.mostrarNotificacion('Repartidor no encontrado para editar.', 'danger');
            return;
        }

        document.getElementById('modalRepartidorLabel').innerHTML = '<i class="fas fa-user-edit"></i> Editar Repartidor';
        const guardarRepartidorBtn = document.getElementById('guardar-repartidor-btn');
        guardarRepartidorBtn.textContent = 'Guardar Cambios';
        guardarRepartidorBtn.dataset.editingId = repartidor.idRepartidor;

        document.getElementById('repartidor-nombre').value = repartidor.nombre;
        if (document.getElementById('repartidor-apellido')) {
            document.getElementById('repartidor-apellido').value = repartidor.apellido || '';
        }
        document.getElementById('repartidor-telefono').value = repartidor.telefono;
        if (document.getElementById('repartidor-id-vehiculo')) {
            // Seleccionar el vehículo asignado en el dropdown
            document.getElementById('repartidor-id-vehiculo').value = repartidor.idVehiculo || '';
        }
        if (document.getElementById('repartidor-contrasena')) {
            document.getElementById('repartidor-contrasena').value = ''; // La contraseña no se precarga por seguridad
            document.getElementById('repartidor-contrasena-group').classList.remove('d-none'); // Asegurarse de que el campo de contraseña sea visible en edición si se decide usar
        }
        if (document.getElementById('repartidor-id-administrador')) {
            document.getElementById('repartidor-id-administrador').value = repartidor.idAdministrador || '1';
        }


        const modal = new bootstrap.Modal(document.getElementById('modalRepartidor'));
        modal.show();
    }

    async confirmarEdicionRepartidor(repartidorId) {
        const nombre = document.getElementById('repartidor-nombre').value;
        const apellido = document.getElementById('repartidor-apellido') ? document.getElementById('repartidor-apellido').value : 'N/A';
        const telefono = document.getElementById('repartidor-telefono').value;
        const contrasena = document.getElementById('repartidor-contrasena') ? document.getElementById('repartidor-contrasena').value : ''; // Vacío si no se cambia
        const idVehiculo = document.getElementById('repartidor-id-vehiculo').value;
        const idVehiculoParsed = idVehiculo ? parseInt(idVehiculo) : null;
        const idAdministrador = parseInt(document.getElementById('repartidor-id-administrador').value) || 1;


        if (!nombre || !telefono || idVehiculoParsed === null || isNaN(idAdministrador) || idAdministrador < 1) {
            this.mostrarNotificacion('Por favor, completa al menos los campos nombre, teléfono, vehículo asignado y ID de administrador del repartidor.', 'warning');
            return;
        }

        const updatedRepartidorData = {
            idRepartidor: repartidorId,
            nombre: nombre,
            apellido: apellido,
            telefono: telefono,
            // Solo incluir la contraseña si se ha ingresado una nueva
            ...(contrasena && { contrasena: contrasena }),
            idVehiculo: idVehiculoParsed,
            idAdministrador: idAdministrador
        };

        try {
            const response = await fetch(`${this.API_BASE_URL}/api/repartidores/${repartidorId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedRepartidorData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`Error al actualizar repartidor: ${errorData.message}`);
            }

            const updatedRepartidor = await response.json();
            const index = this.repartidores.findIndex(r => r.idRepartidor === updatedRepartidor.idRepartidor);
            if (index !== -1) {
                this.repartidores[index] = updatedRepartidor;
            }

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalRepartidor'));
            modalInstance.hide();

            this.actualizarDashboard();
            this.cargarTablaRepartidores();
            this.mostrarNotificacion(`Repartidor ${updatedRepartidor.nombre} ${updatedRepartidor.apellido} actualizado con éxito`, 'success');

        } catch (error) {
            console.error('Error al editar repartidor:', error);
            this.mostrarNotificacion(`Error al editar repartidor: ${error.message}`, 'danger');
        }
    }

    async deleteRepartidor(repartidorId) {
        if (!confirm('¿Estás seguro de que deseas eliminar este repartidor? Esta acción no se puede deshacer.')) {
            return;
        }
        try {
            const response = await fetch(`${this.API_BASE_URL}/api/repartidores/${repartidorId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const errorText = await response.text();
                if (response.status === 409) {
                    throw new Error(`Conflicto de eliminación: ${errorText || 'El repartidor tiene elementos asociados y no puede ser eliminado.'}`);
                }
                throw new Error(`Error al eliminar repartidor: ${errorText || response.statusText}`);
            }

            this.repartidores = this.repartidores.filter(r => r.idRepartidor !== repartidorId);
            this.actualizarDashboard();
            this.cargarTablaRepartidores();
            this.mostrarNotificacion('Repartidor eliminado con éxito.', 'success');
        } catch (error) {
            console.error('Error al eliminar repartidor:', error);
            this.mostrarNotificacion(`Error al eliminar repartidor: ${error.message}`, 'danger');
        }
    }

    getZonaName(zona) {
        const zonas = {
            norte: 'Zona Norte',
            sur: 'Zona Sur',
            este: 'Zona Este',
            oeste: 'Zona Oeste',
            centro: 'Centro'
        };
        return zonas[zona] || zona;
    }

    mostrarNotificacion(mensaje, tipo = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${tipo} position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.innerHTML = `${mensaje} <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>`;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 3000);
    }

    logout() {
        if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
            window.location.href = '../HTML/Index.html';
        }
    }
}

let adminPanel;
document.addEventListener('DOMContentLoaded', () => {
    adminPanel = new AdminPanel();
});