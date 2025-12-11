if (window.lucide) {
    lucide.createIcons();
}

function showToast(message, variant = 'success') {
    const toast = document.getElementById('toast');
    const toastInner = document.getElementById('toast-inner');
    const toastMsg = document.getElementById('toast-message');

    if (!toast || !toastInner || !toastMsg) {
        console.warn('Toast não encontrado no DOM.');
        return;
    }

    toastMsg.textContent = message;

    // Base
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

function confirmDialog(options) {
    const {
        title = "Atenção",
        message = "Tem certeza que deseja continuar?",
        confirmText = "Confirmar",
        cancelText = "Cancelar",
        confirmVariant = "danger",
        onConfirm = () => { },
    } = options || {};

    const modal = document.getElementById("modal-confirm");
    const titleEl = document.getElementById("confirm-title");
    const msgEl = document.getElementById("confirm-message");
    const btnOk = document.getElementById("confirm-ok");
    const btnCancel = document.getElementById("confirm-cancel");

    if (!modal || !titleEl || !msgEl || !btnOk || !btnCancel) {
        console.warn("Modal de confirmação não encontrada no DOM.");
        return;
    }

    titleEl.textContent = title;
    msgEl.textContent = message;
    btnOk.textContent = confirmText;
    btnCancel.textContent = cancelText;

    if (confirmVariant === "danger") {
        btnOk.className =
            "px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-semibold hover:bg-red-700 shadow-sm";
    } else {
        btnOk.className =
            "px-4 py-2 rounded-lg bg-blue-600 text-white text-sm font-semibold hover:bg-blue-700 shadow-sm";
    }

    const close = () => {
        modal.classList.remove("flex");
        btnOk.onclick = null;
        btnCancel.onclick = null;
    };

    btnCancel.onclick = () => close();

    btnOk.onclick = () => {
        close();
        onConfirm();
    };

    modal.classList.add("flex");
}

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    carregarPerfil();
    carregarLogs();

    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
        btnLogout.onclick = logout;
    }

    // Botão "Excluir minha conta"
    const btnDelete = document.getElementById('btn-delete-hospital');
    if (btnDelete) {
        btnDelete.onclick = (e) => {
            e.preventDefault();

            confirmDialog({
                title: "ATENÇÃO",
                message: "Isso apagará permanentemente sua conta de hospital e todos os dados associados a ela. Deseja continuar?",
                confirmText: "Sim, apagar conta",
                cancelText: "Cancelar",
                confirmVariant: "danger",
                onConfirm: async () => {
                    try {
                        await apiFetch('/hospital/me', { method: 'DELETE' });
                        showToast('Conta excluída com sucesso.', 'success');
                        // dá um tempinho pra pessoa ver o toast
                        setTimeout(() => logout(), 1500);
                    } catch (err) {
                        showToast('Erro ao excluir conta: ' + err.message, 'error');
                    }
                }
            });
        };
    }
});

//PERFIL

async function carregarPerfil() {
    try {
        const h = await apiFetch('/hospital/me');
        if (h) {
            document.getElementById('nome').value = h.nomeFantasia;
            document.getElementById('cnpj').value = h.cnpj;
            document.getElementById('email').value = h.emailHospital;
            document.getElementById('telefone').value = h.telefoneHospital;
            document.getElementById('endereco').value = h.endereco;
            document.getElementById('vet').value = h.veterinarioResponsavel;
            document.getElementById('crmv').value = h.crmvVeterinario;
            document.getElementById('classificacao').value = h.classificacaoServico;

            const nomeNav = localStorage.getItem('petone_user_nome');
            if (nomeNav) {
                document.getElementById('user-nome-nav').textContent = nomeNav;
            }
        }
    } catch (e) {
        console.error("Erro ao carregar perfil:", e);
    }
}

document.getElementById('btn-unlock-profile').onclick = function () {
    const inputs = document.querySelectorAll('#form-perfil input:not([readonly])');
    const btnSave = document.getElementById('btn-save-profile');
    const isDisabled = inputs[0].disabled;

    inputs.forEach(i => i.disabled = !isDisabled);
    btnSave.classList.toggle('hidden');
    this.textContent = isDisabled ? "Cancelar" : "Editar";

    if (!isDisabled) carregarPerfil();
};

document.getElementById('form-perfil').onsubmit = async (e) => {
    e.preventDefault();
    const data = {
        nomeFantasia: document.getElementById('nome').value,
        telefoneHospital: document.getElementById('telefone').value,
        endereco: document.getElementById('endereco').value,
        veterinarioResponsavel: document.getElementById('vet').value,
        crmvVeterinario: document.getElementById('crmv').value,
        classificacaoServico: parseInt(document.getElementById('classificacao').value)
    };

    try {
        await apiFetch('/hospital/me', { method: 'PUT', body: JSON.stringify(data) });
        showToast('Perfil atualizado com sucesso!', 'success');
        document.getElementById('btn-unlock-profile').click();
    } catch (e) {
        showToast(`Erro ao salvar: ${e.message}`, 'error');
    }
};

//LOGS DE EMERGÊNCIA

async function carregarLogs() {
    const div = document.getElementById('lista-logs');
    div.innerHTML = '<p class="text-gray-500 text-center py-4 animate-pulse">Buscando chamados...</p>';

    try {
        const logs = await apiFetch('/hospital/logs');

        if (!logs || logs.length === 0) {
            div.innerHTML = '<div class="bg-gray-50 border border-dashed border-gray-300 rounded-xl p-10 text-center text-gray-500">Nenhum chamado de emergência recebido ainda.</div>';
            return;
        }

        div.innerHTML = '';
        logs.sort((a, b) => {
            if (a.status === 'Finalizado' && b.status !== 'Finalizado') return 1;
            if (a.status !== 'Finalizado' && b.status === 'Finalizado') return -1;
            return new Date(b.dataHoraInicio || b.dataHoraRegistro) - new Date(a.dataHoraInicio || a.dataHoraRegistro);
        });

        logs.forEach(l => {
            const isFinalizado = l.status === 'Finalizado';
            const dataHora = l.dataHoraInicio || l.dataHoraRegistro;
            const dateStr = dataHora ? new Date(dataHora).toLocaleString('pt-BR') : 'Data desconhecida';

            const relatorio = l.relatorioMedico || l.relatorio || '';
            const prescricao = l.prescricaoMedicamento || l.prescricao || '';
            const vetFinal = l.veterinarioFinalizacao || l.veterinarioResponsavelFinalizacao || '';
            const crmvFinal = l.crmvFinalizacao || l.crmvVeterinarioFinalizacao || '';

            const card = document.createElement('div');
            card.className = `p-5 rounded-xl border transition relative overflow-hidden ${isFinalizado
                ? 'bg-white border-gray-200 opacity-90'
                : 'bg-white border-red-200 shadow-md border-l-4 border-l-red-500'
                }`;

            card.innerHTML = `
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
            <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                    <span class="font-mono font-bold ${isFinalizado ? 'text-gray-500' : 'text-red-600'} text-lg bg-gray-100 px-2 py-1 rounded">
                        ${l.tokenEmergencia}
                    </span>
                    <span class="text-xs px-2 py-1 rounded-full font-bold uppercase tracking-wide ${isFinalizado
                    ? 'bg-green-100 text-green-800'
                    : 'bg-red-100 text-red-800 animate-pulse'
                }">${l.status}</span>
                    <span class="text-xs text-gray-400 ml-auto md:ml-0">${dateStr}</span>
                </div>
                
                <h4 class="font-bold text-gray-800 text-lg flex items-center gap-2">
                    <i data-lucide="alert-triangle" class="w-5 h-5 ${isFinalizado ? 'text-gray-400' : 'text-red-500'}"></i> 
                    ${l.tipoEmergencia || l.sintomaCausa}
                </h4>
                
                <div class="mt-3 grid grid-cols-1 sm:grid-cols-2 gap-2 text-sm">
                    <p class="text-gray-600">
                      <strong class="text-gray-800">Paciente:</strong> ${l.nomeAnimal} 
                      <span class="text-gray-400">(${l.especieAnimal})</span>
                    </p>
                    <p class="text-gray-600"><strong class="text-gray-800">Tutor:</strong> ${l.nomeCompletoTutor}</p>
                    <p class="text-gray-600">
                      <strong class="text-gray-800">Contato:</strong> 
                      <a href="tel:${l.telefoneTutor}" class="text-blue-600 hover:underline font-medium">
                        ${l.telefoneTutor}
                      </a>
                    </p>
                </div>
            </div>

            <div class="w-full md:w-auto mt-2 md:mt-0 text-right">
                ${!isFinalizado
                    ? `<button onclick="abrirFinalizar('${l.tokenEmergencia}')" class="w-full md:w-auto bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 shadow-sm text-sm font-bold uppercase tracking-wide flex items-center justify-center gap-2 transition transform hover:scale-105">
                         <i data-lucide="check-square" class="w-4 h-4"></i> Atender
                       </button>`
                    : `<div class="text-green-600 flex items-center gap-1 font-medium text-sm bg-green-50 px-3 py-2 rounded-lg border border-green-100 inline-block">
                         <i data-lucide="check" class="w-4 h-4"></i> Concluído
                       </div>`
                }
            </div>
        </div>
        
        ${isFinalizado
                    ? `
              <div class="mt-4 pt-4 border-t border-gray-100 text-sm bg-gray-50 p-3 rounded-lg">
                  <p class="text-gray-700 mb-2">
                    <strong class="text-blue-900 block mb-1">Relatório Médico:</strong> 
                    ${relatorio || 'Nenhum relatório.'}
                  </p>
                  ${prescricao
                        ? `<p class="text-gray-700">
                           <strong class="text-blue-900 block mb-1">Prescrição:</strong> 
                           ${prescricao}
                         </p>`
                        : ''
                    }
                  <p class="text-xs text-gray-400 mt-3 text-right">
                    Finalizado por: ${vetFinal || 'Não informado'} 
                    (CRMV: ${crmvFinal || 'Não informado'})
                  </p>
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
        div.innerHTML = `<p class="text-red-500 text-center">Erro ao carregar logs: ${e.message}</p>`;
    }
}

window.carregarLogs = carregarLogs;

//MODAL FINALIZAÇÃO 

window.abrirFinalizar = (token) => {
    document.getElementById('finalizar-token-input').value = token;
    document.getElementById('modal-token').innerText = token;

    const vetAtual = document.getElementById('vet').value;
    const crmvAtual = document.getElementById('crmv').value;
    if (vetAtual) document.getElementById('finalizar-vet').value = vetAtual;
    if (crmvAtual) document.getElementById('finalizar-crmv').value = crmvAtual;

    document.getElementById('modal-finalizar').style.display = 'flex';
};

document.getElementById('form-finalizar').onsubmit = async (e) => {
    e.preventDefault();
    const token = document.getElementById('finalizar-token-input').value;
    const data = {
        relatorio: document.getElementById('relatorio').value,
        prescricao: document.getElementById('prescricao').value,
        veterinarioResponsavelFinalizacao: document.getElementById('finalizar-vet').value,
        crmvVeterinarioFinalizacao: document.getElementById('finalizar-crmv').value
    };

    try {
        await apiFetch(`/emergencia/finalizar/${token}`, { method: 'PUT', body: JSON.stringify(data) });

        document.getElementById('modal-finalizar').style.display = 'none';
        document.getElementById('form-finalizar').reset();

        showToast('Atendimento finalizado com sucesso!', 'success');
        carregarLogs();
    } catch (e) {
        showToast(`Erro ao finalizar: ${e.message}`, 'error');
    }
};
