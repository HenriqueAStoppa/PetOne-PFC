let userType = 'tutor';

function switchTab(type) {
  userType = type;

  //Alterna aba ativa
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.getElementById(`tab-${type}`).classList.add('active');

  const btn = document.getElementById('btnSubmit');
  const linkCadastro = document.getElementById('link-cadastro');

  //Ajuste dos textos e links de cadastro
  if (type === 'tutor') {
    btn.textContent = 'Entrar como Tutor';
    linkCadastro.href = '/pages/Cadastro/Tutor/cadastro_tutor.html';
  } else {
    btn.textContent = 'Entrar como Hospital';
    linkCadastro.href = '/pages/Cadastro/Hospital/cadastro_hospital.html';
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

  //Estado inicial: aba Tutor
  switchTab('tutor');

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

    if (!valido) return;

    btnSubmit.disabled = true;
    btnSubmit.textContent = "Autenticando...";

    const loginData = {
      email: usuarioInput.value.trim(),
      senha: senhaInput.value.trim()
    };

    try {
      const endpoint = userType === 'tutor'
        ? `${AUTH_URL}/login/tutor`
        : `${AUTH_URL}/login/hospital`;

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginData)
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          loginErrorGlobal.textContent = "Email ou senha incorretos.";
          loginErrorGlobal.style.display = "block";
        } else {
          loginErrorGlobal.textContent = "Erro ao fazer login. Tente novamente.";
          loginErrorGlobal.style.display = "block";
        }
        return;
      }

      const data = await response.json();

      localStorage.setItem('petone_token', data.token);
      localStorage.setItem(
        'petone_user_nome',
        data.nomeCompleto || data.nomeFantasiaHospital || ''
      );
      localStorage.setItem(
        'petone_user_id',
        data.idTutor || data.idHospital || data.id || ''
      );

      if (userType === 'tutor') {
        window.location.href = '/pages/Dashboard/Tutor/dashboard_tutor.html';
      } else {
        window.location.href = '/pages/Dashboard/Hospital/dashboard_hospital.html';
      }

    } catch (error) {
      console.error(error);
      loginErrorGlobal.textContent = "Erro de conexão. Tente novamente.";
      loginErrorGlobal.style.display = "block";
    } finally {
      btnSubmit.disabled = false;
      btnSubmit.textContent = userType === 'tutor'
        ? 'Entrar como Tutor'
        : 'Entrar como Hospital';
    }
  });
});
