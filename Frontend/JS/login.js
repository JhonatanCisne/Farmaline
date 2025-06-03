let currentUser = null;
const bootstrap = window.bootstrap;

const API_BASE_URL = "http://localhost:8080";
const LOGIN_ENDPOINT = `${API_BASE_URL}/api/usuarios/login`;
const REGISTER_ENDPOINT = `${API_BASE_URL}/api/usuarios/registrar`;

document.addEventListener("DOMContentLoaded", () => {
    initializeLoginPage();
});

function initializeLoginPage() {
    setupEventListeners();
    checkExistingSession();
    const contenedor = document.getElementById("contenedor");
    const registrarseBtn = document.getElementById("registrarse");
    const iniciarSesionBtn = document.getElementById("iniciarSesion");

    if (registrarseBtn && contenedor) {
        registrarseBtn.addEventListener("click", () => {
            contenedor.classList.add("activo");
        });
    }

    if (iniciarSesionBtn && contenedor) {
        iniciarSesionBtn.addEventListener("click", () => {
            contenedor.classList.remove("activo");
        });
    }
}

function setupEventListeners() {
    const loginForm = document.getElementById("formularioInicioSesion");
    if (loginForm) {
        loginForm.addEventListener("submit", handleLogin);
    } else {
        console.error("No se encontró el formulario de inicio de sesión con ID 'formularioInicioSesion'.");
    }

    const registerForm = document.getElementById("formularioRegistro");
    if (registerForm) {
        registerForm.addEventListener("submit", handleRegister);
    } else {
        console.error("No se encontró el formulario de registro con ID 'formularioRegistro'.");
    }

    const forgotPasswordForm = document.getElementById("forgotPasswordForm");
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener("submit", handleForgotPassword);
    }

    setupRealTimeValidation();

    const confirmPasswordInput = document.getElementById("contraseña");
    const registerPasswordInput = document.getElementById("contraseña");
    if (confirmPasswordInput && registerPasswordInput) {
        confirmPasswordInput.addEventListener("input", checkPasswordMatch);
        registerPasswordInput.addEventListener("input", function() {
            checkPasswordStrength(this.value);
            checkPasswordMatch();
        });
    }
}

function setupPasswordToggle(buttonId, inputId) {
    const button = document.getElementById(buttonId);
    const input = document.getElementById(inputId);

    if (button && input) {
        button.addEventListener("click", function() {
            const type = input.getAttribute("type") === "password" ? "text" : "password";
            input.setAttribute("type", type);

            const icon = this.querySelector("i");
            if (icon) {
                icon.classList.toggle("fa-eye");
                icon.classList.toggle("fa-eye-slash");
            }
        });
    } else {
        console.warn(`Toggle button (${buttonId}) or input (${inputId}) not found for password toggle.`);
    }
}

async function handleLogin(e) {
    e.preventDefault();

    const form = e.target;
    const emailInput = document.getElementById("correoInicio");
    const passwordInput = document.getElementById("contraseñaInicio");

    let isFormValid = true;
    if (!validateField(emailInput, isEmail(emailInput.value.trim()), "Correo no válido.")) {
        isFormValid = false;
    }
    if (!validateField(passwordInput, passwordInput.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres.")) {
        isFormValid = false;
    }

    if (!isFormValid) {
        return;
    }

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    const submitBtn = form.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Iniciando sesión...");

    try {
        const response = await fetch(LOGIN_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ correoElectronico: email, contrasena: password }),
        });

        if (response.ok) {
            const userData = await response.json();
            sessionStorage.setItem("farmalineUser", JSON.stringify(userData));
            currentUser = userData;

            showAlert("success", `¡Bienvenido, ${userData.nombre || userData.email || userData.correoElectronico}! Inicio de sesión exitoso.`);

            setTimeout(() => {
                window.location.href = "Index.html";
            }, 1500);

        } else if (response.status === 401) {
            showAlert("danger", "Credenciales incorrectas. Por favor verifica tu email y contraseña.");
        } else {
            const errorData = await response.text();
            showAlert("danger", `Error al iniciar sesión: ${errorData || response.statusText}.`);
            console.error("Login error (server response):", response.status, errorData);
        }
    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor. Intenta de nuevo más tarde.");
        console.error("Login fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}

async function handleRegister(e) {
    e.preventDefault();

    const form = e.target;
    const nombreInput = document.getElementById("nombre");
    const correoInput = document.getElementById("correo");
    const passwordInput = document.getElementById("contraseña");

    let isFormValid = true;
    if (!validateField(nombreInput, nombreInput.value.trim() !== "", "El nombre no puede estar vacío.")) {
        isFormValid = false;
    }
    if (!validateField(correoInput, isEmail(correoInput.value.trim()), "Correo no válido.")) {
        isFormValid = false;
    }
    if (!validateField(passwordInput, passwordInput.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres.")) {
        isFormValid = false;
    }

    if (!isFormValid) {
        return;
    }

    const formData = {
        nombre: nombreInput.value.trim(),
        correoElectronico: correoInput.value.trim(),
        password: passwordInput.value.trim(),
    };

    const submitBtn = form.querySelector('button[type="button"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Registrando usuario...");

    try {
        const response = await fetch(REGISTER_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        });

        if (response.ok && response.status === 201) {
            const newUserData = await response.json();
            showAlert("success", "¡Registro exitoso! Ya puedes iniciar sesión con tu cuenta.");

            form.reset();
            form.classList.remove("was-validated");

            setTimeout(() => {
                const contenedor = document.getElementById("contenedor");
                if (contenedor) {
                    contenedor.classList.remove("activo");
                }
                const correoInicioInput = document.getElementById("correoInicio");
                if (correoInicioInput) {
                    correoInicioInput.value = newUserData.correoElectronico;
                }
            }, 1500);

        } else if (response.status === 400) {
            const errorData = await response.text();
            showAlert("danger", `Error de registro: ${errorData || "Datos inválidos o correo ya registrado."}`);
        } else {
            showAlert("danger", `Error al crear la cuenta. Código: ${response.status}. Por favor intenta nuevamente.`);
            console.error("Registration error (server response):", response.status, await response.text());
        }
    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor para registrarte.");
        console.error("Registration fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}

async function handleForgotPassword(e) {
    e.preventDefault();

    const email = document.getElementById("forgotEmail").value;
    const submitBtn = e.target.querySelector('button[type="submit"]');

    setLoadingState(submitBtn, true);
    showAlert("info", "Enviando enlace de recuperación...");

    try {
        await simulateAPICall();

        showAlert("success", "Se ha enviado un enlace de recuperación a tu correo electrónico.");

        const modalElement = document.getElementById("forgotPasswordModal");
        const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        modal.hide();

        document.getElementById("forgotPasswordForm").reset();
    } catch (error) {
        showAlert("danger", "Error al enviar el enlace. Por favor intenta nuevamente.");
    } finally {
        setLoadingState(submitBtn, false);
    }
}

function setupRealTimeValidation() {
    const loginInputs = document.querySelectorAll(".inicio-sesion input[required]");
    const registerInputs = document.querySelectorAll(".registro input[required]");

    loginInputs.forEach((input) => {
        input.addEventListener("blur", function() {
            if (this.id === "correoInicio") {
                validateField(this, isEmail(this.value.trim()), "Correo no válido");
            } else if (this.id === "contraseñaInicio") {
                validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
            } else {
                validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
            }
        });
        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                 if (this.id === "correoInicio") {
                    validateField(this, isEmail(this.value.trim()), "Correo no válido");
                } else if (this.id === "contraseñaInicio") {
                    validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
                } else {
                    validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
                }
            }
        });
    });

    registerInputs.forEach((input) => {
        input.addEventListener("blur", function() {
            if (this.id === "nombre") {
                validateField(this, this.value.trim() !== "", "El nombre no puede estar vacío");
            } else if (this.id === "correo") {
                validateField(this, isEmail(this.value.trim()), "Correo no válido");
            } else if (this.id === "contraseña") {
                validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
            } else {
                validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
            }
        });
        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                 if (this.id === "nombre") {
                    validateField(this, this.value.trim() !== "", "El nombre no puede estar vacío");
                } else if (this.id === "correo") {
                    validateField(this, isEmail(this.value.trim()), "Correo no válido");
                } else if (this.id === "contraseña") {
                    validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
                } else {
                    validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
                }
            }
        });
    });
}

function validateField(field, condition, errorMessage) {
    const formControl = field.closest(".form-control");
    const small = formControl ? formControl.querySelector("small") : null;

    if (condition) {
        field.classList.remove("is-invalid");
        field.classList.add("is-valid");
        if (formControl) formControl.classList.remove("error");
        if (small) small.innerText = "";
        return true;
    } else {
        field.classList.remove("is-valid");
        field.classList.add("is-invalid");
        if (formControl) formControl.classList.add("error");
        if (small) small.innerText = errorMessage;
        return false;
    }
}

function checkPasswordStrength(password) {
    const strengthIndicator = document.getElementById("contraseña").closest(".form-control").querySelector(".password-strength");

    if (!strengthIndicator) {
        const indicator = document.createElement("div");
        indicator.className = "password-strength";
        document.getElementById("contraseña").parentNode.appendChild(indicator);
    }

    let strength = 0;
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength++;
    if (password.match(/[0-9]/)) strength++;
    if (password.match(/[^a-zA-Z0-9]/)) strength++;

    const indicator = document.getElementById("contraseña").closest(".form-control").querySelector(".password-strength");
    if (indicator) {
        indicator.className = "password-strength";
        if (strength <= 1) {
            indicator.classList.add("weak");
            indicator.innerText = "Débil";
        } else if (strength <= 3) {
            indicator.classList.add("medium");
            indicator.innerText = "Media";
        } else {
            indicator.classList.add("strong");
            indicator.innerText = "Fuerte";
        }
    }
}

function checkPasswordMatch() {
    const passwordInput = document.getElementById("contraseña");
    const password = passwordInput.value.trim();
    const confirmPassword = passwordInput.value.trim();

    const match = password === confirmPassword;

    return match;
}

function checkExistingSession() {
    const userData = localStorage.getItem("farmalineUser") || sessionStorage.getItem("farmalineUser");

    if (userData) {
        try {
            currentUser = JSON.parse(userData);
            window.location.href = "Index.html";
        } catch (error) {
            console.error("Error al parsear datos de sesión:", error);
            localStorage.removeItem("farmalineUser");
            sessionStorage.removeItem("farmalineUser");
        }
    }
}

async function simulateAPICall() {
    return new Promise((resolve) => {
        setTimeout(resolve, 500 + Math.random() * 500);
    });
}

function setLoadingState(button, loading) {
    if (button) {
        if (loading) {
            button.classList.add("loading");
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Cargando...';
        } else {
            button.classList.remove("loading");
            button.disabled = false;
            if (button.closest('#formularioInicioSesion')) {
                button.innerHTML = 'Ingresar';
            } else if (button.closest('#formularioRegistro')) {
                button.innerHTML = 'Registrarse';
            } else if (button.closest('#forgotPasswordForm')) {
                button.innerHTML = 'Enviar Enlace';
            }
        }
    }
}

function showAlert(type, message) {
    const existingAlerts = document.querySelectorAll(".alert");
    existingAlerts.forEach((alert) => alert.remove());

    const alertContainer = document.querySelector(".login-section .contenedor");
    if (!alertContainer) {
        console.error("No se encontró el contenedor para las alertas.");
        return;
    }

    const alert = document.createElement("div");
    alert.className = `alert alert-${type} alert-dismissible fade show mt-3`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    alertContainer.insertBefore(alert, alertContainer.firstChild);

    setTimeout(() => {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}

function logout() {
    localStorage.removeItem("farmalineUser");
    sessionStorage.removeItem("farmalineUser");
    currentUser = null;
    window.location.href = "Login.html";
}

window.farmalineAuth = {
    logout,
    getCurrentUser: () => currentUser,
    isLoggedIn: () => currentUser !== null,
};

function isEmail(email) {
    return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(email);
}