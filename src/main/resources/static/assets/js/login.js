// login.js
const API_URL = 'http://localhost:8080';
let userType = 'tutor'; 

function switchTab(type) {
    userType = type;
    
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.getElementById(`tab-${type}`).classList.add('active');
    
    const btn = document.getElementById('btnSubmit');
    const linkCadastro = document.getElementById('link-cadastro');
    
    // Ajuste de caminhos relativos
    if (type === 'tutor') {
        btn.textContent = 'Entrar como Tutor';
        linkCadastro.href = '../../cadastro_tutor.html';
    } else {
        btn.textContent = 'Entrar como Hospital';
        linkCadastro.href = '../../cadastro_hospital.html';
    }
    
    document.getElementById('loginErrorGlobal').style.display = 'none';
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    const usuarioInput = document.getElementById("usuario");
    const senhaInput = document.getElementById("senha");
    const usuarioError = document.getElementById("usuarioError");
    const senhaError = document.getElementById("senhaError");
    const loginErrorGlobal = document.getElementById("loginErrorGlobal");
    const btnSubmit = document.getElementById("btnSubmit");

    form.addEventListener("submit", async function (event) {
      event.preventDefault();

      usuarioError.style.display = "none";
      senhaError.style.display = "none";
      loginErrorGlobal.style.display = "none";
      
      let valido = true;

      if (!usuarioInput.value.trim()) {
        usuarioError.style.display = "block";
        valido = false;
      }
      if (!senhaInput.value.trim()) {
        senhaError.style.display = "block";
        valido = false;
      }

      if (valido) {
        btnSubmit.disabled = true;
        btnSubmit.textContent = "Autenticando...";

        const loginData = {
            email: usuarioInput.value.trim(),
            senha: senhaInput.value.trim()
        };

        try {
            const endpoint = userType === 'tutor' 
                ? `${API_URL}/api/auth/login/tutor` 
                : `${API_URL}/api/auth/login/hospital`;

            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData)
            });

            if (response.ok) {
                const data = await response.json();
                
                localStorage.setItem('petone_token', data.token);
                localStorage.setItem('petone_user_nome', data.nomeCompleto);
                localStorage.setItem('petone_user_id', data.idTutor);

                if (userType === 'tutor') {
                    window.location.href = '../../dashboard_tutor.html';
                } else {
                    window.location.href = '../../dashboard_hospital.html';
                }
            } else {
                loginErrorGlobal.textContent = "Email ou senha incorretos.";
                loginErrorGlobal.style.display = "block";
            }

        } catch (error) {
            loginErrorGlobal.textContent = "Erro de conexão. Tente novamente.";
            loginErrorGlobal.style.display = "block";
        } finally {
            btnSubmit.disabled = false;
            btnSubmit.textContent = userType === 'tutor' ? 'Entrar como Tutor' : 'Entrar como Hospital';
        }
      }
    });
});