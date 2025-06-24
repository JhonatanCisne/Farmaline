class AdminPanel {
    constructor() {
        this.API_BASE_URL = 'http://localhost:8080/api';

        this.productos = [];
        this.repartidores = JSON.parse(localStorage.getItem('farmaline_repartidores')) || [
            { id: 1, nombre: 'Juan Pérez', telefono: '987654321', zona: 'norte' },
            { id: 2, nombre: 'María López', telefono: '912345678', zona: 'centro' }
        ];
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
            repartidorModal.addEventListener('hidden.bs.modal', () => {
                document.getElementById('modalRepartidorLabel').innerHTML = '<i class="fas fa-user-plus"></i> Añadir Nuevo Repartidor';
                const btn = document.getElementById('guardar-repartidor-btn');
                btn.textContent = 'Guardar Repartidor';
                delete btn.dataset.editingId;
                document.getElementById('formRepartidor').reset();
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
        this.actualizarDashboard();
        this.changeSection(this.currentSection);
    }

    async cargarProductosDesdeBackend() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/productos`);
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

    async confirmarActualizacionStock() {
        const productoId = parseInt(document.getElementById('producto-id-stock').value);
        const cantidadAgregar = parseInt(document.getElementById('cantidad-agregar').value);

        if (cantidadAgregar <= 0) {
            this.mostrarNotificacion('La cantidad a añadir debe ser mayor que cero.', 'warning');
            return;
        }

        try {
            const response = await fetch(`${this.API_BASE_URL}/productos/${productoId}/stock?cantidadCambio=${cantidadAgregar}`, {
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
        const fechaCaducidadStr = document.getElementById('producto-fecha-caducidad').value;
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
            fechaCaducidad: fechaCaducidadStr || null,
            fechaIngreso: fechaIngresoStr
        };

        const formData = new FormData();
        formData.append('producto', new Blob([JSON.stringify(newProductData)], { type: 'application/json' }));
        formData.append('file', imagenFile);

        try {
            const response = await fetch(`${this.API_BASE_URL}/productos`, {
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
        const fechaCaducidadStr = document.getElementById('producto-fecha-caducidad').value;
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
            fechaCaducidad: fechaCaducidadStr || null,
            fechaIngreso: fechaIngresoStr
        };

        const formData = new FormData();
        formData.append('producto', new Blob([JSON.stringify(updatedProductData)], { type: 'application/json' }));
        if (imagenFile) {
            formData.append('file', imagenFile);
        }

        try {
            const response = await fetch(`${this.API_BASE_URL}/productos/${productId}`, {
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
            const response = await fetch(`${this.API_BASE_URL}/productos/${productId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al eliminar producto: ${errorText || response.statusText}`);
            }

            // Si la eliminación en el backend fue exitosa (status 200 OK, 204 No Content),
            // actualizamos el array local de productos.
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
                    <td><strong>#${r.id}</strong></td>
                    <td>${r.nombre}</td>
                    <td>${r.telefono}</td>
                    <td>${this.getZonaName(r.zona)}</td>
                    <td><span class="badge bg-success">Activo</span></td>
                    <td>
                        <button class="btn btn-sm btn-warning text-white me-1" onclick="adminPanel.openEditRepartidorModal(${r.id})" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="adminPanel.deleteRepartidor(${r.id})" title="Eliminar">
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

    confirmarNuevoRepartidor() {
        const nombre = document.getElementById('repartidor-nombre').value;
        const telefono = document.getElementById('repartidor-telefono').value;
        const zona = document.getElementById('repartidor-zona').value;

        if (nombre && telefono && zona) {
            const nuevoId = this.repartidores.length > 0 ? Math.max(...this.repartidores.map(r => r.id)) + 1 : 1;
            const nuevoRepartidor = {
                id: nuevoId,
                nombre,
                telefono,
                zona
            };

            this.repartidores.push(nuevoRepartidor);
            localStorage.setItem('farmaline_repartidores', JSON.stringify(this.repartidores));

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalRepartidor'));
            modalInstance.hide();

            document.getElementById('formRepartidor').reset();
            this.actualizarDashboard();
            this.cargarTablaRepartidores();

            this.mostrarNotificacion(`Repartidor ${nombre} creado`, 'success');
        } else {
            this.mostrarNotificacion('Por favor, completa todos los campos del repartidor.', 'danger');
        }
    }

    openEditRepartidorModal(repartidorId) {
        const repartidor = this.repartidores.find(r => r.id === repartidorId);
        if (!repartidor) {
            this.mostrarNotificacion('Repartidor no encontrado para editar.', 'danger');
            return;
        }

        document.getElementById('modalRepartidorLabel').innerHTML = '<i class="fas fa-user-edit"></i> Editar Repartidor';
        const guardarRepartidorBtn = document.getElementById('guardar-repartidor-btn');
        guardarRepartidorBtn.textContent = 'Guardar Cambios';
        guardarRepartidorBtn.dataset.editingId = repartidor.id;

        document.getElementById('repartidor-nombre').value = repartidor.nombre;
        document.getElementById('repartidor-telefono').value = repartidor.telefono;
        document.getElementById('repartidor-zona').value = repartidor.zona;

        const modal = new bootstrap.Modal(document.getElementById('modalRepartidor'));
        modal.show();
    }

    confirmarEdicionRepartidor(repartidorId) {
        const nombre = document.getElementById('repartidor-nombre').value;
        const telefono = document.getElementById('repartidor-telefono').value;
        const zona = document.getElementById('repartidor-zona').value;

        if (!nombre || !telefono || !zona) {
            this.mostrarNotificacion('Por favor, completa todos los campos del repartidor.', 'warning');
            return;
        }

        const repartidorIndex = this.repartidores.findIndex(r => r.id === repartidorId);
        if (repartidorIndex !== -1) {
            this.repartidores[repartidorIndex] = {
                ...this.repartidores[repartidorIndex],
                nombre,
                telefono,
                zona
            };
            localStorage.setItem('farmaline_repartidores', JSON.stringify(this.repartidores));

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalRepartidor'));
            modalInstance.hide();

            this.actualizarDashboard();
            this.cargarTablaRepartidores();
            this.mostrarNotificacion(`Repartidor ${nombre} actualizado con éxito`, 'success');
        } else {
            this.mostrarNotificacion('Error: Repartidor no encontrado para actualizar.', 'danger');
        }
    }


    deleteRepartidor(repartidorId) {
        if (!confirm('¿Estás seguro de que deseas eliminar este repartidor? Esta acción no se puede deshacer.')) {
            return;
        }
        this.repartidores = this.repartidores.filter(r => r.id !== repartidorId);
        localStorage.setItem('farmaline_repartidores', JSON.stringify(this.repartidores));
        this.actualizarDashboard();
        this.cargarTablaRepartidores();
        this.mostrarNotificacion('Repartidor eliminado con éxito.', 'success');
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