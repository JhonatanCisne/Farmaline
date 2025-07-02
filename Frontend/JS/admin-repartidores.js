document.addEventListener("DOMContentLoaded", () => {
  const API_BASE_URL = "http://localhost:8080/api"

  // Elementos del DOM
  const btnNuevoRepartidor = document.getElementById("btn-nuevo-repartidor")
  const btnAplicarFiltros = document.getElementById("repartidores-apply-filters-btn")
  const btnClearFiltros = document.getElementById("repartidores-clear-filters-btn")
  const guardarRepartidorBtn = document.getElementById("guardar-repartidor-btn")

  // Filtros
  const filtroNombre = document.getElementById("repartidores-filtro-nombre")
  const filtroApellido = document.getElementById("repartidores-filtro-apellido")
  const filtroCorreo = document.getElementById("repartidores-filtro-correo")

  // Tabla
  const tablaRepartidoresBody = document.getElementById("repartidores-table-body")

  // Modal y formulario
  let modalRepartidor = null
  const formRepartidor = document.getElementById("form-repartidor")
  const repartidorId = document.getElementById("repartidor-id-input")
  const repartidorNombre = document.getElementById("repartidor-nombre-input")
  const repartidorApellido = document.getElementById("repartidor-apellido-input")
  const repartidorCorreo = document.getElementById("repartidor-correo-input")
  const repartidorTelefono = document.getElementById("repartidor-telefono-input")
  const repartidorPlaca = document.getElementById("repartidor-placa-input")
  const repartidorContrasena = document.getElementById("repartidor-contrasena-input")
  const repartidorContrasenaGroup = document.getElementById("repartidor-contrasena-group")

  // Estado
  let repartidores = []

  // Event Listeners
  if (btnNuevoRepartidor) {
    btnNuevoRepartidor.addEventListener("click", () => openRepartidorModal())
  }

  if (guardarRepartidorBtn) {
    guardarRepartidorBtn.addEventListener("click", () => saveRepartidor())
  }

  if (btnAplicarFiltros) {
    btnAplicarFiltros.addEventListener("click", () => aplicarFiltros())
  }

  if (btnClearFiltros) {
    btnClearFiltros.addEventListener("click", () => clearFilters())
  }

  if (filtroNombre) {
    filtroNombre.addEventListener("input", () => aplicarFiltros())
  }

  if (filtroApellido) {
    filtroApellido.addEventListener("input", () => aplicarFiltros())
  }

  if (filtroCorreo) {
    filtroCorreo.addEventListener("input", () => aplicarFiltros())
  }

  // Funciones
  function openRepartidorModal(repartidorId = null) {
    formRepartidor.reset()
    repartidorId.value = ""

    repartidorContrasenaGroup.style.display = "block"
    repartidorContrasena.value = ""

    if (repartidorId) {
      const repartidor = repartidores.find((r) => r.idRepartidor === repartidorId)
      if (repartidor) {
        repartidorId.value = repartidor.idRepartidor
        repartidorNombre.value = repartidor.nombre
        repartidorApellido.value = repartidor.apellido
        repartidorCorreo.value = repartidor.correo_Electronico
        repartidorTelefono.value = repartidor.telefono
        repartidorPlaca.value = repartidor.placa || ""
        repartidorContrasenaGroup.style.display = "none"
      }
    }

    if (window.bootstrap) {
      modalRepartidor = new window.bootstrap.Modal(document.getElementById("modal-repartidor"))
      modalRepartidor.show()
    }
  }

  async function saveRepartidor() {
    const repartidorIdValue = repartidorId.value
    const isEditing = !!repartidorIdValue

    const repartidorData = {
      nombre: repartidorNombre.value,
      apellido: repartidorApellido.value,
      correo_Electronico: repartidorCorreo.value,
      telefono: repartidorTelefono.value,
      placa: repartidorPlaca.value,
    }

    if (repartidorContrasena.value) {
      repartidorData.contrasena = repartidorContrasena.value
    } else if (!isEditing) {
      alert("La contraseña es obligatoria para nuevos repartidores.")
      return
    }

    try {
      const url = isEditing ? `${API_BASE_URL}/repartidores/${repartidorIdValue}` : `${API_BASE_URL}/repartidores`
      const method = isEditing ? "PUT" : "POST"

      const response = await fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(repartidorData),
      })

      if (!response.ok) {
        throw new Error("Error al guardar repartidor")
      }

      modalRepartidor.hide()
      await fetchRepartidores()
      aplicarFiltros()
      alert("Repartidor guardado exitosamente.")
    } catch (error) {
      console.error("Error al guardar repartidor:", error)
      alert("Error al guardar repartidor: " + error.message)
    }
  }

  async function fetchRepartidores() {
    try {
      const response = await fetch(`${API_BASE_URL}/repartidores`)
      if (!response.ok) {
        throw new Error("Error al obtener repartidores")
      }
      repartidores = await response.json()
      renderRepartidores(repartidores)
    } catch (error) {
      console.error("Error fetching repartidores:", error)
      tablaRepartidoresBody.innerHTML =
        '<tr><td colspan="7" class="text-center py-4 text-danger">Error al cargar los repartidores.</td></tr>'
    }
  }

  function renderRepartidores(repartidoresToRender) {
    tablaRepartidoresBody.innerHTML = ""

    if (repartidoresToRender.length === 0) {
      tablaRepartidoresBody.innerHTML =
        '<tr><td colspan="7" class="text-center">No hay repartidores disponibles.</td></tr>'
      return
    }

    repartidoresToRender.forEach((repartidor) => {
      const row = document.createElement("tr")
      row.innerHTML = `
                <td>${repartidor.idRepartidor}</td>
                <td>${repartidor.nombre} ${repartidor.apellido}</td>
                <td>${repartidor.correo_Electronico}</td>
                <td>${repartidor.telefono}</td>
                <td>${repartidor.placa || "N/A"}</td>
                <td>Admin</td>
                <td>
                    <button class="btn btn-sm btn-info edit-repartidor-btn" data-id="${repartidor.idRepartidor}"><i class="fas fa-edit"></i></button>
                    <button class="btn btn-sm btn-danger delete-repartidor-btn" data-id="${repartidor.idRepartidor}"><i class="fas fa-trash"></i></button>
                </td>
            `
      tablaRepartidoresBody.appendChild(row)
    })

    // Event listeners para botones de acción
    tablaRepartidoresBody.querySelectorAll(".edit-repartidor-btn").forEach((button) => {
      button.addEventListener("click", (e) => openRepartidorModal(Number.parseInt(e.currentTarget.dataset.id)))
    })

    tablaRepartidoresBody.querySelectorAll(".delete-repartidor-btn").forEach((button) => {
      button.addEventListener("click", (e) => deleteRepartidor(Number.parseInt(e.currentTarget.dataset.id)))
    })
  }

  function aplicarFiltros() {
    const nombreFiltro = filtroNombre ? filtroNombre.value.toLowerCase() : ""
    const apellidoFiltro = filtroApellido ? filtroApellido.value.toLowerCase() : ""
    const correoFiltro = filtroCorreo ? filtroCorreo.value.toLowerCase() : ""

    const repartidoresFiltrados = repartidores.filter((repartidor) => {
      const nombreCoincide = repartidor.nombre.toLowerCase().includes(nombreFiltro)
      const apellidoCoincide = repartidor.apellido.toLowerCase().includes(apellidoFiltro)
      const correoCoincide = repartidor.correo_Electronico.toLowerCase().includes(correoFiltro)

      return nombreCoincide && apellidoCoincide && correoCoincide
    })

    renderRepartidores(repartidoresFiltrados)
  }

  function clearFilters() {
    if (filtroNombre) filtroNombre.value = ""
    if (filtroApellido) filtroApellido.value = ""
    if (filtroCorreo) filtroCorreo.value = ""
    aplicarFiltros()
  }

  async function deleteRepartidor(repartidorId) {
    if (!confirm("¿Estás seguro de que quieres eliminar este repartidor?")) {
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/repartidores/${repartidorId}`, {
        method: "DELETE",
      })

      if (!response.ok) {
        throw new Error("Error al eliminar repartidor")
      }

      await fetchRepartidores()
      aplicarFiltros()
      alert("Repartidor eliminado exitosamente.")
    } catch (error) {
      console.error("Error deleting repartidor:", error)
      alert("Error al eliminar repartidor: " + error.message)
    }
  }

  // Inicializar
  fetchRepartidores()
})
