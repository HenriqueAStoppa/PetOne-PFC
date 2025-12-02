document.addEventListener('DOMContentLoaded', () => {
    checkAuth(); // api.js

    // Carregar animais
    apiFetch('/animais')
        .then(animais => {
            const select = document.getElementById('animal');
            if(!animais || animais.length === 0) {
                select.innerHTML = '<option disabled>Nenhum animal cadastrado</option>';
                alert('Você precisa cadastrar um animal antes!');
                window.location.href = '/pages/Dashboard/Tutor/dashboard_tutor.html';
            } else {
                select.innerHTML = '<option value="" disabled selected>Selecione...</option>';
                animais.forEach(a => {
                    select.innerHTML += `<option value="${a.idAnimal}">${a.nomeAnimal} (${a.especie})</option>`;
                });
            }
        })
        .catch(() => alert("Erro ao carregar animais. Verifique a conexão."));

    document.getElementById('formEmergencia').addEventListener('submit', (e) => {
        e.preventDefault();
        
        const btnSubmit = e.target.querySelector('button[type="submit"]');
        const textoOriginal = "SOLICITAR SOCORRO IMEDIATO";

        btnSubmit.disabled = true;
        btnSubmit.innerText = "Buscando hospital...";

        const idAnimal = document.getElementById('animal').value;
        const tipoEmergencia = document.getElementById('sintoma').value;

        if (!navigator.geolocation) {
            alert('Seu navegador não suporta geolocalização.');
            restaurarBotao();
            return;
        }

        const options = { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 };

        navigator.geolocation.getCurrentPosition(async (position) => {
            const latitudeTutor = position.coords.latitude;
            const longitudeTutor = position.coords.longitude;
            
            const data = { idAnimal, tipoEmergencia, latitudeTutor, longitudeTutor };

            try {
                const result = await apiFetch('/emergencia/iniciar', {
                    method: 'POST',
                    body: JSON.stringify(data)
                });

                if(result) {
                    // Preenche o Modal
                    document.getElementById('tokenDisplay').innerText = result.tokenEmergencia;
                    document.getElementById('hospitalNome').innerText = result.hospitalNome;
                    document.getElementById('hospitalEndereco').innerText = result.hospitalEndereco;

                    const enderecoCodificado = encodeURIComponent(result.hospitalEndereco);
                    
                    // URL de navegação corrigida
                    const mapsUrl = `https://www.google.com/maps/dir/?api=1&origin=${latitudeTutor},${longitudeTutor}&destination=${enderecoCodificado}`;

                    document.getElementById('btnRota').href = mapsUrl;
                    document.getElementById('modalSucesso').style.display = 'flex';
                } else {
                    restaurarBotao();
                }
            } catch(err) {
                alert('Erro ao registrar emergência: ' + err.message);
                restaurarBotao();
            }
        }, (error) => {
            console.error(error);
            alert('Não foi possível obter sua localização. Libere a permissão de localização.');
            restaurarBotao();
        }, options);
        
        function restaurarBotao() {
            btnSubmit.disabled = false;
            btnSubmit.innerText = textoOriginal;
        }
    });
});