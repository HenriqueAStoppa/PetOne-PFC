if (window.lucide) {
    lucide.createIcons();
}

let cacheAnimais = [];

function showToast(message, variant = 'success') {
    const toast = document.getElementById('toast');
    const toastInner = document.getElementById('toast-inner');
    const toastMsg = document.getElementById('toast-message');

    if (!toast || !toastInner || !toastMsg) {
        console.warn('Toast não encontrado no DOM.');
        return;
    }

    toastMsg.textContent = message;

    toastInner.className = 'px-4 py-3 rounded-lg shadow-lg flex items-center gap-2 text-sm';

    if (variant === 'success') {
        toastInner.classList.add('bg-green-600', 'text-white');
    } else if (variant === 'error') {
        toastInner.classList.add('bg-red-600', 'text-white');
    } else {
        toastInner.classList.add('bg-blue-600', 'text-white');
    }

    toast.classList.remove('hidden');

    setTimeout(() => {
        toast.classList.add('hidden');
    }, 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    carregarHeader();
    carregarPerfilTutor();
    carregarAnimais();
    carregarLogs();

    document.getElementById('btn-logout').addEventListener('click', logout);
});

function carregarHeader() {
    const nomeUser = localStorage.getItem('petone_user_nome');
    if (nomeUser) document.getElementById('user-nome-nav').textContent = `Olá, ${nomeUser.split(' ')[0]}`;
}

function iniciarEmergencia() {
    window.location.href = '/pages/Emergencia/emergencia.html';
}

async function carregarPerfilTutor() {
    try {
        const tutor = await apiFetch('/tutor/me');
        if (tutor) {
            document.getElementById('perfil-nome').value = tutor.nomeCompleto;
            document.getElementById('perfil-email').value = tutor.emailTutor;
            document.getElementById('perfil-telefone').value = tutor.telefoneTutor;
            document.getElementById('perfil-nascimento').value = tutor.dataNascimento;
        }
    } catch (e) {
        console.error(e);
    }
}

async function carregarAnimais() {
    const div = document.getElementById('lista-animais');
    div.innerHTML = '<p class="text-gray-400 col-span-2 text-center py-10">Carregando...</p>';

    try {
        cacheAnimais = await apiFetch('/animais');
        if (!cacheAnimais || cacheAnimais.length === 0) {
            div.innerHTML = '<div class="col-span-2 text-center py-10 bg-white rounded-xl border border-dashed border-gray-300"><p class="text-gray-500">Você ainda não tem pets cadastrados.</p></div>';
            return;
        }
        div.innerHTML = '';
        cacheAnimais.forEach(a => {
            const card = document.createElement('div');
            card.className = 'bg-white p-5 rounded-xl border border-gray-200 hover:shadow-md transition flex justify-between items-center group relative overflow-hidden';
            card.innerHTML = `
                <div class="absolute top-0 left-0 w-1 h-full bg-verde-escuro"></div>
                <div class="pl-3">
                    <div class="flex items-center gap-2">
                        <h4 class="font-bold text-lg text-verde-escuro">${a.nomeAnimal}</h4>
                        <span class="bg-verde-claro text-verde-escuro text-xs px-2 py-0.5 rounded-full font-semibold">${a.especie}</span>
                    </div>
                    <p class="text-sm text-gray-600 mt-1">${a.raca}, ${a.idade} anos • ${a.sexo}</p>
                    ${a.usaMedicacao ? `<p class="text-xs text-red-500 font-medium mt-1 flex items-center gap-1"><i data-lucide="pill" class="w-3 h-3"></i> ${a.qualMedicacao}</p>` : ''}
                </div>
                <div class="flex gap-2">
                    <button onclick="editarAnimal('${a.idAnimal}')" class="p-2 text-gray-400 hover:text-verde-escuro transition" title="Editar"><i data-lucide="edit-2" class="w-5 h-5"></i></button>
                    <button onclick="deletarAnimal('${a.idAnimal}')" class="p-2 text-gray-400 hover:text-red-600 transition" title="Excluir"><i data-lucide="trash" class="w-5 h-5"></i></button>
                </div>
            `;
            div.appendChild(card);
        });
        if (window.lucide) {
            lucide.createIcons();
        }
    } catch (e) {
        console.error(e);
        div.innerHTML = `<p class="text-red-500 col-span-2 text-center">Erro ao carregar animais.</p>`;
    }
}

// Histórico de emergências do tutor
async function carregarLogs() {
    const div = document.getElementById('lista-logs');
    try {
        const logs = await apiFetch('/tutor/logs');

        if (!logs || !logs.length) {
            div.innerHTML = '<p class="text-gray-400 text-xs text-center py-4">Nenhum registro encontrado.</p>';
            return;
        }

        div.innerHTML = '';

        logs.forEach(l => {
            const dataBruta = l.dataHoraInicio || l.dataHoraRegistro;
            const date = dataBruta
                ? new Date(dataBruta).toLocaleDateString('pt-BR')
                : '';

            const statusFinalizado = l.status === 'Finalizado';
            const statusClass = statusFinalizado
                ? 'bg-gray-100 text-gray-600'
                : 'bg-yellow-100 text-yellow-800';

            const relatorio = l.relatorioMedico || l.relatorio || '';
            const prescricao = l.prescricaoMedicamento || l.prescricao || '';

            const card = document.createElement('div');
            card.className = 'p-3 rounded-lg border border-gray-100 hover:bg-gray-50 transition text-sm mb-2';

            card.innerHTML = `
                <div class="flex justify-between mb-1 items-center">
                    <span class="font-bold text-verde-escuro text-xs truncate w-32" title="${l.tipoEmergencia}">
                        ${l.tipoEmergencia}
                    </span>
                    <span class="text-[10px] px-2 py-0.5 rounded-full uppercase font-bold tracking-wide ${statusClass}">
                        ${l.status}
                    </span>
                </div>

                <p class="text-gray-600 text-xs flex items-center gap-1">
                    <i data-lucide="paw-print" class="w-3 h-3"></i>
                    ${l.nomeAnimal} ${date ? '• ' + date : ''}
                </p>
                <p class="text-gray-500 text-xs mt-1 truncate flex items-center gap-1">
                    <i data-lucide="building" class="w-3 h-3"></i>
                    ${l.nomeFantasiaHospital || 'Hospital não informado'}
                </p>

                ${statusFinalizado && (relatorio || prescricao)
                    ? `
                        <div class="mt-2 p-2 bg-gray-50 rounded-lg border border-gray-100 text-[11px] text-gray-700">
                            <p class="mb-1">
                                <span class="font-semibold text-gray-900">Relatório Médico:</span>
                                ${relatorio || 'Não informado.'}
                            </p>
                            ${prescricao
                        ? `<p>
                                         <span class="font-semibold text-gray-900">Prescrição:</span>
                                         ${prescricao}
                                       </p>`
                        : ''
                    }
                        </div>
                        `
                    : ''
                }
            `;

            div.appendChild(card);
        });

        if (window.lucide) {
            lucide.createIcons();
        }
    } catch (e) {
        console.error(e);
        div.innerHTML = '<p class="text-red-500 text-xs text-center">Erro ao carregar logs.</p>';
    }
}

// Modais e Forms 

const modal = document.getElementById('modal-animal');
document.getElementById('btn-show-add-animal').onclick = () => {
    document.getElementById('form-animal').reset();
    document.getElementById('animal-id').value = '';
    document.getElementById('modal-animal-titulo').textContent = 'Adicionar Pet';
    document.getElementById('div-qual-medicacao').style.display = 'none';
    modal.style.display = 'flex';
};
document.getElementById('btn-close-modal-animal').onclick = () => modal.style.display = 'none';

document.getElementById('animal-medicacao').addEventListener('change', function () {
    document.getElementById('div-qual-medicacao').style.display = this.checked ? 'block' : 'none';
});

window.editarAnimal = (id) => {
    const pet = cacheAnimais.find(a => a.idAnimal === id);
    if (!pet) return;
    document.getElementById('animal-id').value = pet.idAnimal;
    document.getElementById('animal-nome').value = pet.nomeAnimal;
    document.getElementById('animal-idade').value = pet.idade;
    document.getElementById('animal-especie').value = pet.especie;
    document.getElementById('animal-raca').value = pet.raca;
    document.querySelector(`input[name="sexo"][value="${pet.sexo}"]`).checked = true;
    document.getElementById('animal-castrado').checked = pet.castrado;
    document.getElementById('animal-medicacao').checked = pet.usaMedicacao;
    document.getElementById('animal-qual-medicacao').value = pet.qualMedicacao || '';
    document.getElementById('div-qual-medicacao').style.display = pet.usaMedicacao ? 'block' : 'none';
    document.getElementById('modal-animal-titulo').textContent = 'Editar Pet';
    modal.style.display = 'flex';
};

window.deletarAnimal = async (id) => {
    if (confirm("Tem certeza que deseja remover este pet?")) {
        try {
            await apiFetch(`/animais/${id}`, { method: 'DELETE' });
            showToast('Pet removido com sucesso.', 'success');
            carregarAnimais();
        } catch (e) {
            showToast(e.message || 'Erro ao remover pet.', 'error');
        }
    }
};

document.getElementById('form-animal').onsubmit = async (e) => {
    e.preventDefault();
    const id = document.getElementById('animal-id').value;
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/animais/${id}` : '/animais';
    const data = {
        nomeAnimal: document.getElementById('animal-nome').value,
        idade: document.getElementById('animal-idade').value,
        especie: document.getElementById('animal-especie').value,
        raca: document.getElementById('animal-raca').value,
        sexo: document.querySelector('input[name="sexo"]:checked').value,
        castrado: document.getElementById('animal-castrado').checked,
        usaMedicacao: document.getElementById('animal-medicacao').checked,
        qualMedicacao: document.getElementById('animal-qual-medicacao').value
    };
    try {
        await apiFetch(url, { method, body: JSON.stringify(data) });
        modal.style.display = 'none';
        showToast('Pet salvo com sucesso!', 'success');
        carregarAnimais();
    } catch (e) {
        showToast(e.message || 'Erro ao salvar pet.', 'error');
    }
};

document.getElementById('form-perfil-tutor').onsubmit = async (e) => {
    e.preventDefault();
    const data = {
        nomeCompleto: document.getElementById('perfil-nome').value,
        telefoneTutor: document.getElementById('perfil-telefone').value,
        dataNascimento: document.getElementById('perfil-nascimento').value
    };
    try {
        await apiFetch('/tutor/me', { method: 'PUT', body: JSON.stringify(data) });
        showToast("Perfil atualizado com sucesso!", 'success');
        carregarPerfilTutor();
    } catch (e) {
        showToast(e.message || 'Erro ao atualizar perfil.', 'error');
    }
};

document.getElementById('btn-delete-perfil').onclick = async (e) => {
    e.preventDefault();
    if (confirm("ATENÇÃO: Isso apagará sua conta e todos os seus dados permanentemente. Continuar?")) {
        try {
            await apiFetch('/tutor/me', { method: 'DELETE' });
            showToast('Conta excluída com sucesso.', 'success');
            setTimeout(() => logout(), 1500);
        } catch (e) {
            showToast(e.message || 'Erro ao excluir conta.', 'error');
        }
    }
};
