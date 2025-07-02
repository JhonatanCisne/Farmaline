document.addEventListener("DOMContentLoaded", () => {
  const API_BASE_URL = "http://localhost:8080/api"

  // Elementos del DOM
  const btnNuevoProducto = document.getElementById("btn-nuevo-producto")
  const applyFiltersBtn = document.getElementById("productos-apply-filters-btn")
  const clearFiltersBtn = document.getElementById("productos-clear-filters-btn")
  const guardarProductoBtn = document.getElementById("guardar-producto-btn")

  // Filtros
  const filterNombre = document.getElementById("productos-filter-nombre")
  const filterCantidad = document.getElementById("productos-filter-cantidad")
  const filterCantidadOpcion = document.getElementById("productos-filter-cantidad-opcion")
  const filterFechaCaducidad = document.getElementById("productos-filter-fecha-caducidad")
  const filterIndicator = document.getElementById("productos-filter-indicator")

  // Tabla
  const tablaProductosBody = document.getElementById("productos-table-body")

  // Modal y formulario
  let modalProducto = null
  const formProducto = document.getElementById("form-producto")
  const productoId = document.getElementById("producto-id-input")
  const productoNombre = document.getElementById("producto-nombre-input")
  const productoDescripcion = document.getElementById("producto-descripcion-input")
  const productoStock = document.getElementById("producto-stock-input")
  const productoPrecio = document.getElementById("producto-precio-input")
  const productoFechaCaducidad = document.getElementById("producto-fecha-caducidad-input")
  const productoIgv = document.getElementById("producto-igv-input")
  const productoPrecioFinal = document.getElementById("producto-precio-final-input")
  const productoImagenFile = document.getElementById("producto-imagen-file-input")
  const productoImagenUrl = document.getElementById("producto-imagen-url-input")
  const imagenPreview = document.getElementById("producto-imagen-preview")

  // Estado
  let productos = []

  // Event Listeners
  if (btnNuevoProducto) {
    btnNuevoProducto.addEventListener("click", () => openProductModal())
  }

  if (guardarProductoBtn) {
    guardarProductoBtn.addEventListener("click", () => saveProduct())
  }

  if (productoPrecio) {
    productoPrecio.addEventListener("input", () => calcularPrecioFinal())
  }

  if (productoIgv) {
    productoIgv.addEventListener("input", () => calcularPrecioFinal())
  }

  if (productoImagenFile) {
    productoImagenFile.addEventListener("change", (event) => previewImage(event))
  }

  if (applyFiltersBtn) {
    applyFiltersBtn.addEventListener("click", () => aplicarFiltros())
  }

  if (clearFiltersBtn) {
    clearFiltersBtn.addEventListener("click", () => limpiarFiltros())
  }

  if (filterNombre) {
    filterNombre.addEventListener("input", () => aplicarFiltros())
  }

  // Funciones
  function calcularPrecioFinal() {
    const precioSinIgv = Number.parseFloat(productoPrecio.value) || 0
    const igvPorcentaje = Number.parseFloat(productoIgv.value) || 0
    const precioFinal = precioSinIgv * (1 + igvPorcentaje)
    productoPrecioFinal.value = precioFinal.toFixed(2)
  }

  function previewImage(event) {
    const [file] = event.target.files
    if (file) {
      imagenPreview.innerHTML = `<img src="${URL.createObjectURL(file)}" class="img-thumbnail" style="max-width: 100px; max-height: 100px; object-fit: cover;">`
    } else {
      imagenPreview.innerHTML = ""
    }
  }

  function openProductModal(productId = null) {
    formProducto.reset()
    productoId.value = ""
    imagenPreview.innerHTML = ""
    productoImagenUrl.value = ""

    if (productId) {
      const product = productos.find((p) => p.idProducto === productId)
      if (product) {
        productoId.value = product.idProducto
        productoNombre.value = product.nombre
        productoDescripcion.value = product.descripcion
        productoStock.value = product.stockDisponible
        productoPrecio.value = product.precio
        productoFechaCaducidad.value = product.fechaCaducidad || ""
        productoIgv.value = product.igv
        productoPrecioFinal.value = product.precioFinal
        productoImagenUrl.value = product.imagen || ""

        if (product.imagen) {
          imagenPreview.innerHTML = `<img src="${product.imagen}" class="img-thumbnail" style="max-width: 100px; max-height: 100px; object-fit: cover;">`
        }
      }
    } else {
      calcularPrecioFinal()
    }

    if (window.bootstrap) {
      modalProducto = new window.bootstrap.Modal(document.getElementById("modal-producto"))
      modalProducto.show()
    }
  }

  async function saveProduct() {
    const productId = productoId.value
    const isEditing = !!productId

    try {
      const productData = {
        nombre: productoNombre.value,
        descripcion: productoDescripcion.value,
        stockDisponible: Number.parseInt(productoStock.value),
        precio: Number.parseFloat(productoPrecio.value),
        fechaCaducidad: productoFechaCaducidad.value || null,
        igv: Number.parseFloat(productoIgv.value),
        precioFinal: Number.parseFloat(productoPrecioFinal.value),
        imagen: productoImagenUrl.value,
        fechaIngreso: new Date().toISOString().slice(0, 10),
      }

      if (isEditing) {
        productData.idProducto = Number.parseInt(productId)
      }

      const url = isEditing ? `${API_BASE_URL}/productos/${productId}` : `${API_BASE_URL}/productos`
      const method = isEditing ? "PUT" : "POST"

      const response = await fetch(url, {
        method: method,
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(productData),
      })

      if (!response.ok) {
        throw new Error("Error al guardar producto")
      }

      modalProducto.hide()
      await fetchProducts()
      aplicarFiltros()
      alert("Producto guardado exitosamente.")
    } catch (error) {
      console.error("Error al guardar producto:", error)
      alert("Error al guardar producto: " + error.message)
    }
  }

  async function fetchProducts() {
    try {
      const response = await fetch(`${API_BASE_URL}/productos`)
      if (!response.ok) {
        throw new Error("Error al obtener productos")
      }
      productos = await response.json()
      renderProducts(productos)
    } catch (error) {
      console.error("Error fetching products:", error)
      tablaProductosBody.innerHTML =
        '<tr><td colspan="11" class="text-center py-4 text-danger">Error al cargar los productos.</td></tr>'
    }
  }

  function renderProducts(productsToRender) {
    tablaProductosBody.innerHTML = ""

    if (productsToRender.length === 0) {
      tablaProductosBody.innerHTML =
        '<tr><td colspan="11" class="text-center py-4">No hay productos disponibles.</td></tr>'
      return
    }

    productsToRender.forEach((product) => {
      const row = document.createElement("tr")
      row.innerHTML = `
                <td>${product.idProducto}</td>
                <td>${product.nombre}</td>
                <td>${product.descripcion}</td>
                <td>${product.stockDisponible}</td>
                <td>$${product.precio.toFixed(2)}</td>
                <td><img src="${product.imagen}" alt="${product.nombre}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"></td>
                <td>${product.fechaCaducidad || "N/A"}</td>
                <td>${product.fechaIngreso}</td>
                <td>${(product.igv * 100).toFixed(0)}%</td>
                <td>$${product.precioFinal.toFixed(2)}</td>
                <td>
                    <button class="btn btn-sm btn-info edit-product-btn" data-id="${product.idProducto}"><i class="fas fa-edit"></i></button>
                    <button class="btn btn-sm btn-danger delete-product-btn" data-id="${product.idProducto}"><i class="fas fa-trash-alt"></i></button>
                </td>
            `
      tablaProductosBody.appendChild(row)
    })

    // Event listeners para botones de acción
    tablaProductosBody.querySelectorAll(".edit-product-btn").forEach((button) => {
      button.addEventListener("click", (e) => openProductModal(Number.parseInt(e.currentTarget.dataset.id)))
    })

    tablaProductosBody.querySelectorAll(".delete-product-btn").forEach((button) => {
      button.addEventListener("click", (e) => deleteProduct(Number.parseInt(e.currentTarget.dataset.id)))
    })
  }

  function aplicarFiltros() {
    const filtros = {}
    let indicadorTexto = ""

    if (filterNombre.value.trim() !== "") {
      filtros.nombre = filterNombre.value.trim()
      indicadorTexto += `Nombre: "${filtros.nombre}" `
    }

    if (filterCantidad.value !== "") {
      filtros.cantidad = Number.parseFloat(filterCantidad.value)
      filtros.cantidadOpcion = filterCantidadOpcion.value
      indicadorTexto += `Cantidad ${filtros.cantidadOpcion === "mayor" ? ">=" : "<="} ${filtros.cantidad} `
    }

    if (filterFechaCaducidad.value !== "") {
      filtros.fechaCaducidad = filterFechaCaducidad.value
      indicadorTexto += `Caducidad hasta: ${filtros.fechaCaducidad} `
    }

    if (indicadorTexto === "") {
      filterIndicator.textContent = "Sin filtros aplicados"
    } else {
      filterIndicator.textContent = `Filtros: ${indicadorTexto.trim()}`
    }

    cargarProductos(filtros)
  }

  function limpiarFiltros() {
    filterNombre.value = ""
    filterCantidad.value = ""
    filterCantidadOpcion.value = "mayor"
    filterFechaCaducidad.value = ""
    filterIndicator.textContent = "Sin filtros aplicados"
    cargarProductos({})
  }

  async function cargarProductos(filtros = {}) {
    try {
      const url = new URL(`${API_BASE_URL}/productos`)

      if (filtros.nombre) {
        url.searchParams.append("nombre", filtros.nombre)
      }

      if (filtros.cantidad !== undefined && filtros.cantidad !== null && filtros.cantidad !== "") {
        if (filtros.cantidadOpcion === "mayor") {
          url.searchParams.append("stockMinimo", filtros.cantidad)
        } else if (filtros.cantidadOpcion === "menor") {
          url.searchParams.append("stockMaximo", filtros.cantidad)
        }
      }

      if (filtros.fechaCaducidad) {
        url.searchParams.append("fechaCaducidadHasta", filtros.fechaCaducidad)
      }

      const response = await fetch(url)
      if (!response.ok) {
        if (response.status === 204) {
          tablaProductosBody.innerHTML =
            '<tr><td colspan="11" class="text-center py-4">No hay productos disponibles.</td></tr>'
          return
        }
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const productos = await response.json()
      renderProducts(productos)
    } catch (error) {
      console.error("Error al cargar productos:", error)
      tablaProductosBody.innerHTML =
        '<tr><td colspan="11" class="text-center py-4 text-danger">Error al cargar los productos.</td></tr>'
    }
  }

  async function deleteProduct(productId) {
    if (!confirm("¿Estás seguro de que quieres eliminar este producto?")) {
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/productos/${productId}`, {
        method: "DELETE",
      })

      if (!response.ok) {
        throw new Error("Error al eliminar producto")
      }

      await fetchProducts()
      aplicarFiltros()
      alert("Producto eliminado exitosamente.")
    } catch (error) {
      console.error("Error deleting product:", error)
      alert("Error al eliminar producto: " + error.message)
    }
  }

  // Verificar si hay filtros en la URL (para navegación desde inicio)
  function checkUrlFilters() {
    const urlParams = new URLSearchParams(window.location.search)
    const filter = urlParams.get("filter")

    if (filter) {
      switch (filter) {
        case "critico":
          filterCantidadOpcion.value = "menor"
          filterCantidad.value = "10"
          break
        case "bajo":
          filterCantidadOpcion.value = "menor"
          filterCantidad.value = "50"
          break
      }
      aplicarFiltros()
    }
  }

  // Inicializar
  fetchProducts()
  checkUrlFilters()
})
