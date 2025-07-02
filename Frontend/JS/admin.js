document.addEventListener("DOMContentLoaded", () => {
  const toggleSidebarBtn = document.getElementById("toggle-sidebar-btn")
  const sidebar = document.getElementById("admin-sidebar")
  const logoutBtn = document.getElementById("logout-btn")

  // Toggle sidebar para móvil
  if (toggleSidebarBtn && sidebar) {
    toggleSidebarBtn.addEventListener("click", () => {
      sidebar.classList.toggle("active")
      document.body.classList.toggle("sidebar-open")
    })
  }

  // Cerrar sidebar al hacer clic fuera
  document.body.addEventListener("click", (event) => {
    if (
      sidebar &&
      sidebar.classList.contains("active") &&
      !sidebar.contains(event.target) &&
      toggleSidebarBtn &&
      !toggleSidebarBtn.contains(event.target)
    ) {
      sidebar.classList.remove("active")
      document.body.classList.remove("sidebar-open")
    }
  })

  // Responsive
  window.addEventListener("resize", () => {
    if (window.innerWidth > 992 && sidebar) {
      sidebar.classList.remove("active")
      document.body.classList.remove("sidebar-open")
    }
  })

  // Logout functionality
  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault()
      if (confirm("¿Está seguro que desea cerrar sesión?")) {
        // Limpiar datos de sesión si los hay
        localStorage.removeItem("adminSession")
        sessionStorage.clear()

        // Redireccionar al login
        window.location.href = "Login.html"
      }
    })
  }
})
