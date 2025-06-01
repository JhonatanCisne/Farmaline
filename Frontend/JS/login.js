// Variables globales
let currentUser = null
const bootstrap = window.bootstrap // Declare the bootstrap variable

// Inicializar cuando el DOM esté listo
document.addEventListener("DOMContentLoaded", () => {
  initializeLoginPage()
})

// Inicializar la página de login
function initializeLoginPage() {
  setupEventListeners()
  checkExistingSession()
}

// Configurar event listeners
function setupEventListeners() {
  // Formulario de login
  document.getElementById("loginForm").addEventListener("submit", handleLogin)

  // Formulario de registro
  document.getElementById("registerForm").addEventListener("submit", handleRegister)

  // Formulario de recuperación de contraseña
  document.getElementById("forgotPasswordForm").addEventListener("submit", handleForgotPassword)

  // Toggle password visibility
  setupPasswordToggle("toggleLoginPassword", "loginPassword")
  setupPasswordToggle("toggleRegisterPassword", "registerPassword")
  setupPasswordToggle("toggleConfirmPassword", "confirmPassword")

  // Validación en tiempo real
  setupRealTimeValidation()

  // Verificar coincidencia de contraseñas
  document.getElementById("confirmPassword").addEventListener("input", checkPasswordMatch)
  document.getElementById("registerPassword").addEventListener("input", function () {
    checkPasswordStrength(this.value)
    checkPasswordMatch()
  })
}

// Configurar toggle de contraseña
function setupPasswordToggle(buttonId, inputId) {
  const button = document.getElementById(buttonId)
  const input = document.getElementById(inputId)

  button.addEventListener("click", function () {
    const type = input.getAttribute("type") === "password" ? "text" : "password"
    input.setAttribute("type", type)

    const icon = this.querySelector("i")
    icon.classList.toggle("fa-eye")
    icon.classList.toggle("fa-eye-slash")
  })
}

// Manejar login
async function handleLogin(e) {
  e.preventDefault()

  const form = e.target
  const email = document.getElementById("loginEmail").value
  const password = document.getElementById("loginPassword").value
  const remember = document.getElementById("rememberMe").checked

  // Validar formulario
  if (!form.checkValidity()) {
    form.classList.add("was-validated")
    return
  }

  // Mostrar loading
  const submitBtn = form.querySelector('button[type="submit"]')
  setLoadingState(submitBtn, true)

  try {
    // Simular llamada a API
    await simulateAPICall()

    // Verificar credenciales (simulado)
    const loginSuccess = await validateCredentials(email, password)

    if (loginSuccess) {
      // Guardar sesión
      const userData = {
        email: email,
        name: "Usuario Demo",
        loginTime: new Date().toISOString(),
      }

      if (remember) {
        localStorage.setItem("farmalineUser", JSON.stringify(userData))
      } else {
        sessionStorage.setItem("farmalineUser", JSON.stringify(userData))
      }

      showAlert("success", "¡Bienvenido! Inicio de sesión exitoso.")

      // Redirigir después de 2 segundos
      setTimeout(() => {
        window.location.href = "Index.html"
      }, 2000)
    } else {
      showAlert("danger", "Credenciales incorrectas. Por favor verifica tu email y contraseña.")
    }
  } catch (error) {
    showAlert("danger", "Error al iniciar sesión. Por favor intenta nuevamente.")
    console.error("Login error:", error)
  } finally {
    setLoadingState(submitBtn, false)
  }
}

// Manejar registro
async function handleRegister(e) {
  e.preventDefault()

  const form = e.target

  // Validar formulario
  if (!form.checkValidity()) {
    form.classList.add("was-validated")
    return
  }

  // Verificar que las contraseñas coincidan
  if (!checkPasswordMatch()) {
    return
  }

  const formData = {
    firstName: document.getElementById("firstName").value,
    lastName: document.getElementById("lastName").value,
    email: document.getElementById("registerEmail").value,
    phone: document.getElementById("phone").value,
    birthDate: document.getElementById("birthDate").value,
    password: document.getElementById("registerPassword").value,
    newsletter: document.getElementById("newsletter").checked,
  }

  // Mostrar loading
  const submitBtn = form.querySelector('button[type="submit"]')
  setLoadingState(submitBtn, true)

  try {
    // Simular llamada a API
    await simulateAPICall()

    // Verificar si el email ya existe
    const emailExists = await checkEmailExists(formData.email)

    if (emailExists) {
      showAlert("danger", "Este correo electrónico ya está registrado. Intenta con otro o inicia sesión.")
      return
    }

    // Registrar usuario (simulado)
    const registrationSuccess = await registerUser(formData)

    if (registrationSuccess) {
      showAlert("success", "¡Registro exitoso! Ya puedes iniciar sesión con tu cuenta.")

      // Limpiar formulario
      form.reset()
      form.classList.remove("was-validated")

      // Cambiar a pestaña de login
      setTimeout(() => {
        document.getElementById("login-tab").click()
        document.getElementById("loginEmail").value = formData.email
      }, 2000)
    }
  } catch (error) {
    showAlert("danger", "Error al crear la cuenta. Por favor intenta nuevamente.")
    console.error("Registration error:", error)
  } finally {
    setLoadingState(submitBtn, false)
  }
}

// Manejar recuperación de contraseña
async function handleForgotPassword(e) {
  e.preventDefault()

  const email = document.getElementById("forgotEmail").value
  const submitBtn = e.target.querySelector('button[type="submit"]')

  setLoadingState(submitBtn, true)

  try {
    // Simular envío de email
    await simulateAPICall()

    showAlert("success", "Se ha enviado un enlace de recuperación a tu correo electrónico.")

    // Cerrar modal
    const modal = bootstrap.Modal.getInstance(document.getElementById("forgotPasswordModal"))
    modal.hide()

    // Limpiar formulario
    document.getElementById("forgotPasswordForm").reset()
  } catch (error) {
    showAlert("danger", "Error al enviar el enlace. Por favor intenta nuevamente.")
  } finally {
    setLoadingState(submitBtn, false)
  }
}

// Configurar validación en tiempo real
function setupRealTimeValidation() {
  const inputs = document.querySelectorAll("input[required]")

  inputs.forEach((input) => {
    input.addEventListener("blur", function () {
      validateField(this)
    })

    input.addEventListener("input", function () {
      if (this.classList.contains("is-invalid")) {
        validateField(this)
      }
    })
  })
}

// Validar campo individual
function validateField(field) {
  const isValid = field.checkValidity()

  field.classList.remove("is-valid", "is-invalid")

  if (field.value.trim() !== "") {
    field.classList.add(isValid ? "is-valid" : "is-invalid")
  }

  return isValid
}

// Verificar fortaleza de contraseña
function checkPasswordStrength(password) {
  const strengthIndicator = document.querySelector(".password-strength")

  if (!strengthIndicator) {
    // Crear indicador si no existe
    const indicator = document.createElement("div")
    indicator.className = "password-strength"
    document.getElementById("registerPassword").parentNode.appendChild(indicator)
  }

  let strength = 0

  if (password.length >= 6) strength++
  if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength++
  if (password.match(/[0-9]/)) strength++
  if (password.match(/[^a-zA-Z0-9]/)) strength++

  const indicator = document.querySelector(".password-strength")
  indicator.className = "password-strength"

  if (strength <= 1) {
    indicator.classList.add("weak")
  } else if (strength <= 2) {
    indicator.classList.add("medium")
  } else {
    indicator.classList.add("strong")
  }
}

// Verificar coincidencia de contraseñas
function checkPasswordMatch() {
  const password = document.getElementById("registerPassword").value
  const confirmPassword = document.getElementById("confirmPassword").value
  const confirmField = document.getElementById("confirmPassword")

  if (confirmPassword === "") {
    confirmField.classList.remove("is-valid", "is-invalid")
    return true
  }

  const match = password === confirmPassword

  confirmField.classList.remove("is-valid", "is-invalid")
  confirmField.classList.add(match ? "is-valid" : "is-invalid")

  const feedback = confirmField.nextElementSibling
  if (feedback && feedback.classList.contains("invalid-feedback")) {
    feedback.textContent = match ? "" : "Las contraseñas no coinciden."
  }

  return match
}

// Verificar sesión existente
function checkExistingSession() {
  const userData = localStorage.getItem("farmalineUser") || sessionStorage.getItem("farmalineUser")

  if (userData) {
    try {
      currentUser = JSON.parse(userData)
      // Si ya hay sesión, redirigir al index
      window.location.href = "Index.html"
    } catch (error) {
      // Si hay error al parsear, limpiar storage
      localStorage.removeItem("farmalineUser")
      sessionStorage.removeItem("farmalineUser")
    }
  }
}

// Funciones de simulación de API
async function simulateAPICall() {
  return new Promise((resolve) => {
    setTimeout(resolve, 1000 + Math.random() * 1000)
  })
}

async function validateCredentials(email, password) {
  // Simulación: aceptar cualquier email con contraseña "123456"
  return password === "123456" || email === "demo@farmaline.com"
}

async function checkEmailExists(email) {
  // Simulación: algunos emails ya existen
  const existingEmails = ["admin@farmaline.com", "test@test.com"]
  return existingEmails.includes(email)
}

async function registerUser(userData) {
  // Simulación: siempre exitoso
  console.log("Registering user:", userData)
  return true
}

// Utilidades
function setLoadingState(button, loading) {
  if (loading) {
    button.classList.add("loading")
    button.disabled = true
  } else {
    button.classList.remove("loading")
    button.disabled = false
  }
}

function showAlert(type, message) {
  // Remover alertas existentes
  const existingAlerts = document.querySelectorAll(".alert")
  existingAlerts.forEach((alert) => alert.remove())

  // Crear nueva alerta
  const alert = document.createElement("div")
  alert.className = `alert alert-${type} alert-dismissible fade show`
  alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `

  // Insertar al inicio del contenido
  const cardBody = document.querySelector(".card-body")
  cardBody.insertBefore(alert, cardBody.firstChild)

  // Auto-remover después de 5 segundos
  setTimeout(() => {
    if (alert.parentNode) {
      alert.remove()
    }
  }, 5000)
}

// Función para logout (para usar en otras páginas)
function logout() {
  localStorage.removeItem("farmalineUser")
  sessionStorage.removeItem("farmalineUser")
  currentUser = null
  window.location.href = "Login.html"
}

// Exportar funciones para uso global
window.farmalineAuth = {
  logout,
  getCurrentUser: () => currentUser,
  isLoggedIn: () => currentUser !== null,
}

// ========================================
// FUNCIONALIDAD DEL FORMULARIO ANIMADO
// ========================================

// Selección del contenedor principal y los botones de alternar
const contenedor = document.getElementById("contenedor")
const registrarseBtn = document.getElementById("registrarse")
const iniciarSesionBtn = document.getElementById("iniciarSesion")

// Agregar evento de clic al botón de "Registrarse"
if (registrarseBtn) {
  registrarseBtn.addEventListener("click", () => {
    contenedor.classList.add("activo") // Añadir clase "activo" al contenedor
  })
}

// Agregar evento de clic al botón de "Iniciar Sesión"
if (iniciarSesionBtn) {
  iniciarSesionBtn.addEventListener("click", () => {
    contenedor.classList.remove("activo") // Eliminar clase "activo" del contenedor
  })
}

// Funcionalidad adicional para formularios animados
document.addEventListener("DOMContentLoaded", () => {
  // Referencias a los elementos de los formularios
  const formularioRegistro = document.getElementById("formularioRegistro")
  const formularioInicioSesion = document.getElementById("formularioInicioSesion")
  const nombre = document.getElementById("nombre")
  const correo = document.getElementById("correo")
  const contraseña = document.getElementById("contraseña")
  const correoInicio = document.getElementById("correoInicio")
  const contraseñaInicio = document.getElementById("contraseñaInicio")

  // Solo ejecutar si los elementos existen (para evitar errores en otras páginas)
  if (formularioRegistro && formularioInicioSesion) {
    // Validación del formulario de registro
    formularioRegistro.addEventListener("submit", (e) => {
      e.preventDefault()
      if (checkInputsRegistro()) {
        alert("Registro exitoso.")
        window.location.href = "Index.html"
      }
    })

    // Validación del formulario de inicio de sesión
    formularioInicioSesion.addEventListener("submit", (e) => {
      e.preventDefault()
      if (checkInputsInicio()) {
        alert("Inicio de sesión exitoso.")
        window.location.href = "Index.html"
      }
    })

    // Validaciones dinámicas en el registro
    if (nombre) {
      nombre.addEventListener("input", () =>
        validateField(nombre, nombre.value.trim() !== "", "El nombre no puede estar vacío"),
      )
    }
    if (correo) {
      correo.addEventListener("input", () => validateField(correo, isEmail(correo.value.trim()), "Correo no válido"))
    }
    if (contraseña) {
      contraseña.addEventListener("input", () =>
        validateField(
          contraseña,
          contraseña.value.trim().length >= 8,
          "La contraseña debe tener al menos 8 caracteres",
        ),
      )
    }

    // Validaciones dinámicas en el inicio de sesión
    if (correoInicio) {
      correoInicio.addEventListener("input", () =>
        validateField(correoInicio, isEmail(correoInicio.value.trim()), "Correo no válido"),
      )
    }
    if (contraseñaInicio) {
      contraseñaInicio.addEventListener("input", () =>
        validateField(
          contraseñaInicio,
          contraseñaInicio.value.trim().length >= 8,
          "La contraseña debe tener al menos 8 caracteres",
        ),
      )
    }

    // Agregar funcionalidad a los botones de envío del formulario
    document.querySelectorAll('button[type="button"]').forEach((button) => {
      button.addEventListener("click", () => {
        // Simular almacenamiento de sesión
        sessionStorage.setItem(
          "farmalineUser",
          JSON.stringify({
            name: nombre?.value || "Usuario",
            email: correo?.value || correoInicio?.value || "usuario@ejemplo.com",
            isLoggedIn: true,
          }),
        )
      })
    })
  }
})

// Funciones de validación para formularios animados
function checkInputsRegistro() {
  let isValid = true
  const nombre = document.getElementById("nombre")
  const correo = document.getElementById("correo")
  const contraseña = document.getElementById("contraseña")

  if (nombre) validateField(nombre, nombre.value.trim() !== "", "El nombre no puede estar vacío")
  if (correo) validateField(correo, isEmail(correo.value.trim()), "Correo no válido")
  if (contraseña)
    validateField(contraseña, contraseña.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres")

  document.querySelectorAll(".registro .form-control").forEach((control) => {
    if (control.classList.contains("error")) {
      isValid = false
    }
  })
  return isValid
}

function checkInputsInicio() {
  let isValid = true
  const correoInicio = document.getElementById("correoInicio")
  const contraseñaInicio = document.getElementById("contraseñaInicio")

  if (correoInicio) validateField(correoInicio, isEmail(correoInicio.value.trim()), "Correo no válido")
  if (contraseñaInicio)
    validateField(
      contraseñaInicio,
      contraseñaInicio.value.trim().length >= 8,
      "La contraseña debe tener al menos 8 caracteres",
    )

  document.querySelectorAll(".inicio-sesion .form-control").forEach((control) => {
    if (control.classList.contains("error")) {
      isValid = false
    }
  })
  return isValid
}

function isEmail(email) {
  return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(email)
}
