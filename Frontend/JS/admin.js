// admin.js
class AdminPanel {
    constructor() {
        // La URL base de tu API REST. ¡Asegúrate de que esta URL sea correcta!
        // Por defecto, asumo que tu backend corre en http://localhost:8080
        this.API_BASE_URL = 'http://localhost:8080/api'; 

        this.productos = []; // Los productos ahora se cargarán desde el backend.
        // Los repartidores siguen siendo datos mock, ya que no tienes un controlador de backend para ellos.
        this.repartidores = JSON.parse(localStorage.getItem('farmaline_repartidores')) || [
            { id: 1, nombre: 'Juan Pérez', telefono: '987654321', zona: 'norte' },
            { id: 2, nombre: 'María López', telefono: '912345678', zona: 'centro' }
        ];
        this.currentSection = 'inicio';
        this.currentCategory = '';
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
                    // Actualiza el indicador visual del filtro de stock
                    if (filter === 'critico') {
                        document.getElementById('filter-indicator').innerHTML = '<span class="badge bg-danger ms-2">Stock Crítico</span>';
                    } else if (filter === 'bajo') {
                        document.getElementById('filter-indicator').innerHTML = '<span class="badge bg-warning text-dark ms-2">Stock Bajo</span>';
                    } else {
                        document.getElementById('filter-indicator').innerHTML = '';
                    }
                    this.changeSection('productos');
                    // `changeSection` ya llama a `cargarTablaProductos`, no es necesario aquí.
                } else if (action === 'repartidores') {
                    this.changeSection('repartidores');
                }
            });
        });

        document.getElementById('cantidad-agregar').addEventListener('input', () => {
            this.calcularStockNuevo();
        });
    }

    async render() {
        // Al iniciar, cargamos los productos desde el backend.
        await this.cargarProductosDesdeBackend();
        this.actualizarDashboard();
        this.changeSection(this.currentSection);
    }

    // ---
    // ## Interacción con el Backend (API REST)

    // ### Cargar Productos desde el Backend
    // Esta función ahora hace una llamada `GET` a tu endpoint `/api/productos` para obtener todos los productos.

    // Si no hay contenido (estado 204), simplemente vacía la lista local de productos.
    async cargarProductosDesdeBackend() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/productos`);
            if (!response.ok) {
                // Manejar específicamente el caso de "No Content" (204)
                if (response.status === 204) {
                    this.productos = []; // No hay productos en el servidor
                    this.mostrarNotificacion('No se encontraron productos en el servidor.', 'info');
                } else {
                    // Para otros errores HTTP (4xx, 5xx)
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } else {
                this.productos = await response.json();
            }
        } catch (error) {
            console.error('Error al cargar productos desde el backend:', error);
            this.mostrarNotificacion('Error al cargar productos. Intenta recargar la página.', 'danger');
            this.productos = []; // Vaciar productos si hay un error de conexión o parseo
        }
        // Después de cargar (o fallar), actualiza la tabla y el dashboard.
        this.cargarTablaProductos();
        this.actualizarDashboard();
    }
    // ---

    // ### Confirmar Actualización de Stock
    // Esta función envía una solicitud `PUT` a tu endpoint `/api/productos/{id}/stock` para actualizar la cantidad de stock de un producto.

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
                // No se necesita body si la cantidad es un @RequestParam
            });

            if (!response.ok) {
                const errorText = await response.text(); // Intenta leer el mensaje de error del backend
                throw new Error(`Error al actualizar stock: ${errorText || response.statusText}`);
            }

            const updatedProducto = await response.json(); // El backend devuelve el ProductoDTO actualizado

            // Actualizar el producto en el array local del frontend.
            const index = this.productos.findIndex(p => p.id === updatedProducto.id);
            if (index !== -1) {
                this.productos[index].stock = updatedProducto.stock;
                // También podrías actualizar otros campos si `updatedProducto` los trae y fueran relevantes
                this.productos[index].name = updatedProducto.name; 
                this.productos[index].category = updatedProducto.category;
            }

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalStock'));
            modalInstance.hide();

            this.actualizarDashboard(); // Recalcula los totales del dashboard
            this.cargarTablaProductos(); // Vuelve a renderizar la tabla para reflejar el cambio

            this.mostrarNotificacion(`Stock de ${updatedProducto.name} actualizado a ${updatedProducto.stock}`, 'success');

        } catch (error) {
            console.error('Error al actualizar stock:', error);
            this.mostrarNotificacion(`Error al actualizar stock: ${error.message}`, 'danger');
        }
    }
    // ---

    // ### Confirmar Nuevo Producto
    // Esta función envía una solicitud `POST` a tu endpoint `/api/productos` para crear un nuevo producto.

    async confirmarNuevoProducto() {
        const nombre = document.getElementById('producto-nombre').value;
        const categoria = document.getElementById('producto-categoria').value;
        const stockInicial = parseInt(document.getElementById('producto-stock-inicial').value);

        if (!nombre || !categoria || isNaN(stockInicial) || stockInicial < 0) {
            this.mostrarNotificacion('Por favor, completa todos los campos del producto correctamente.', 'warning');
            return;
        }

        // Se envían los datos mínimos requeridos por tu ProductoDTO.
        // Los campos como `description`, `price`, `igv`, `finalPrice`, `expirationDate`, `entryDate`
        // se inicializan con valores por defecto si no hay campos en el formulario para ellos.
        // **IMPORTANTE**: Asegúrate de que estos valores por defecto sean válidos para tu backend.
        const newProductData = {
            name: nombre,
            category: categoria,
            stock: stockInicial,
            description: "Sin descripción", // Valor por defecto
            price: 0.0, // Valor por defecto (BigDecimal en backend)
            igv: 0.0, // Valor por defecto (BigDecimal en backend)
            finalPrice: 0.0, // Valor por defecto (BigDecimal en backend)
            expirationDate: new Date().toISOString().split('T')[0], // Fecha actual en formato YYYY-MM-DD
            entryDate: new Date().toISOString().split('T')[0] // Fecha actual en formato YYYY-MM-DD
        };

        try {
            const response = await fetch(`${this.API_BASE_URL}/productos`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newProductData)
            });

            if (!response.ok) {
                // Intenta leer el mensaje de error del backend, asumiendo que es JSON.
                const errorData = await response.json().catch(() => ({ message: response.statusText })); 
                throw new Error(`Error al crear producto: ${errorData.message}`);
            }

            const createdProducto = await response.json();
            this.productos.push(createdProducto); // Añadir el nuevo producto al array local.

            const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalNuevoProducto'));
            modalInstance.hide();

            document.getElementById('formNuevoProducto').reset(); // Limpia el formulario
            this.actualizarDashboard(); // Actualiza los contadores del dashboard
            this.cargarTablaProductos(); // Vuelve a renderizar la tabla

            this.mostrarNotificacion(`Producto "${createdProducto.name}" añadido con éxito (ID: ${createdProducto.id})`, 'success');

        } catch (error) {
            console.error('Error al añadir nuevo producto:', error);
            this.mostrarNotificacion(`Error al añadir producto: ${error.message}`, 'danger');
        }
    }
    // ---

    // ## Funciones de Navegación y Renderizado
    
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
                this.currentCategory = ''; // Reinicia el filtro de categoría
                document.querySelectorAll('.category-btn').forEach(btn => {
                    btn.classList.remove('active');
                });
                document.querySelector('[data-category=""]').classList.add('active'); // Activa el botón "Todos"
                this.cargarTablaProductos(); // Recarga la tabla de productos con los filtros aplicados
                break;
            case 'repartidores':
                pageTitle.textContent = 'Repartidores';
                this.cargarTablaRepartidores();
                break;
        }

        // Limpiar filtros de stock al cambiar de sección (a menos que vayas a la sección de productos)
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
    // ---

    // ## Funciones de Dashboard y Tablas
    
    // Actualizado: Ahora usa `this.productos` que se cargan del backend
    actualizarDashboard() {
        const totalProductos = this.productos.length;
        const stockCritico = this.productos.filter(p => p.stock <= 10).length;
        const stockBajo = this.productos.filter(p => p.stock > 10 && p.stock <= 20).length;
        const totalRepartidores = this.repartidores.length; // Repartidores siguen siendo locales

        document.getElementById('total-productos').textContent = totalProductos;
        document.getElementById('stock-critico').textContent = stockCritico;
        document.getElementById('stock-bajo').textContent = stockBajo;
        document.getElementById('total-repartidores').textContent = totalRepartidores;
    }

    filtrarPorCategoria(category) {
        this.currentCategory = category;
        this.currentStockFilter = ''; // Limpiar filtro de stock cuando se filtra por categoría
        document.getElementById('filter-indicator').innerHTML = '';

        document.querySelectorAll('.category-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-category="${category}"]`).classList.add('active');

        this.cargarTablaProductos();
    }

    // Actualizado: Usa `this.productos` que se cargan del backend
    cargarTablaProductos() {
        const tbody = document.getElementById('tabla-productos');
        let productosFiltrados = this.productos;

        // Filtrar por categoría
        // Si tu backend NO envía un campo 'categoria' en el JSON del producto,
        // este filtro no funcionará correctamente. Considera añadirlo a tu ProductoDTO.
        if (this.currentCategory) {
            // Usa p.categoria si ese es el nombre de la propiedad en tu JSON
            productosFiltrados = productosFiltrados.filter(p => p.categoria === this.currentCategory);
        }

        // Filtrar por stock (aplicado después del filtro de categoría)
        // Usa 'stockDisponible' para los filtros, como en tu JSON
        if (this.currentStockFilter === 'critico') {
            productosFiltrados = productosFiltrados.filter(p => p.stockDisponible <= 10);
        } else if (this.currentStockFilter === 'bajo') {
            productosFiltrados = productosFiltrados.filter(p => p.stockDisponible > 10 && p.stockDisponible <= 20);
        }

        tbody.innerHTML = productosFiltrados.map(p => `
            <tr>
                <td><strong>#${p.idProducto}</strong></td>  <td>${p.nombre}</td>                      <td>
                    <span class="badge ${this.getStockBadgeClass(p.stockDisponible)}">${p.stockDisponible}</span> </td>
                <td>
                    <button class="btn btn-sm btn-teal" onclick="adminPanel.abrirModalStock(${p.idProducto})">
                        <i class="fas fa-plus"></i> Aumentar
                    </button>
                </td>
            </tr>
        `).join('');

        if (productosFiltrados.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">
                        <i class="fas fa-search fa-2x mb-2"></i><br>
                        No se encontraron productos con los filtros aplicados
                    </td>
                </tr>
            `;
        }
    }

    // Usa 'stockDisponible' para determinar la clase del badge
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
                </tr>
            `).join('');
        }
    }

    abrirModalStock(productoId) {
        const producto = this.productos.find(p => p.id === productoId);
        if (producto) {
            document.getElementById('producto-id-stock').value = producto.id;
            document.getElementById('producto-nombre-stock').value = producto.name;
            document.getElementById('stock-actual').value = producto.stock;
            document.getElementById('cantidad-agregar').value = 10; // Valor inicial sugerido
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
            localStorage.setItem('farmaline_repartidores', JSON.stringify(this.repartidores)); // Los repartidores siguen siendo locales
            
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

    getStockBadgeClass(stock) {
        if (stock <= 10) return 'bg-danger';
        if (stock <= 20) return 'bg-warning text-dark';
        return 'bg-success';
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
            window.location.href = '../HTML/Index.html'; // Redirige a tu página de inicio de sesión o principal
        }
    }
}

// Inicializa el panel de administración una vez que el DOM esté completamente cargado.
let adminPanel;
document.addEventListener('DOMContentLoaded', () => {
    adminPanel = new AdminPanel();
});