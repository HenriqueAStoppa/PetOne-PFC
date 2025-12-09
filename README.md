# PetOne – Emergência Veterinária

## 1. Visão Geral

O **PetOne – Emergência Veterinária** é um sistema web que auxilia tutores de animais a encontrarem rapidamente um hospital veterinário parceiro em casos de emergência, organizando o fluxo desde o cadastro do tutor/animal até o registro do atendimento emergencial.

O sistema foi desenvolvido como Projeto de Conclusão de Curso em Engenharia de Software e contempla:
- Backend em **Java 17 + Spring Boot**
- Banco de dados **NoSQL (MongoDB)**
- Frontend em **HTML/CSS/JavaScript (jQuery/Bootstrap/Tailwind)**
- Autenticação baseada em **JWT**
- Envio de e-mail para **recuperação de senha**
- Termos de **Consentimento e Privacidade (LGPD)** para Tutor e Hospital

---

## 2. Objetivos do Sistema

- Facilitar o **registro de emergências veterinárias**.
- Conectar rapidamente o **tutor** a um **hospital parceiro**.
- Centralizar informações relevantes sobre:
  - Tutor
  - Animal
  - Hospital
  - Histórico de atendimentos de emergência
- Garantir **segurança e privacidade** dos dados pessoais conforme a LGPD.

---

## 3. Público-Alvo

- **Tutores** de animais de estimação (usuários finais).
- **Hospitais veterinários parceiros**, responsáveis pelo atendimento das emergências.

---

## 4. Escopo Funcional (Principais Funcionalidades)

### 4.1 Módulo Tutor
- Cadastro de tutor com:
  - Nome completo
  - CPF
  - E-mail
  - Telefone
  - Data de nascimento
  - Senha (armazenada como hash)
- Login com e-mail + senha
- Recuperação de senha via e-mail (token de redefinição)
- Aceite do **Termo de Consentimento e Privacidade (LGPD)** no momento do cadastro

### 4.2 Módulo Hospital
- Cadastro de hospital com:
  - Nome fantasia
  - CNPJ
  - E-mail
  - Telefone
  - Endereço
  - Responsável técnico (veterinário) e CRMV
- Login do hospital
- Visualização e atualização de dados do próprio perfil
- Aceite do **Termo de Consentimento e Privacidade (LGPD)** no momento do cadastro

### 4.3 Módulo Animal
- Cadastro de animal vinculado a um Tutor:
  - Nome
  - Idade
  - Espécie
  - Raça
  - Sexo
  - Informação se é castrado
  - Uso de medicação contínua (e qual medicação)

### 4.4 Módulo Emergência
- Início de uma emergência pelo tutor logado:
  - Seleção do animal
  - Envio dos dados da emergência
- Geração de **token único de emergência**
- Seleção do hospital parceiro mais próximo (regra de negócio simulada)
- Registro dos dados da emergência no log

### 4.5 Módulo Log de Emergência
- Armazena um registro completo do atendimento, incluindo:
  - Data/hora de início da emergência
  - Dados do tutor
  - Dados do animal
  - Dados do hospital
  - Token de emergência

### 4.6 Autenticação e Segurança
- Autenticação com **JWT (JSON Web Token)**.
- Proteção de endpoints sensíveis.
- Criptografia de senha com **BCrypt**.
- Filtro de requisição para validar o token em cada chamada autenticada.

---

## 5. Arquitetura do Sistema

### 5.1 Visão Geral

O sistema segue uma arquitetura em camadas:

- **Camada de Apresentação (Frontend)**
  - Páginas HTML, CSS, JavaScript, jQuery
  - Comunicação com a API via `fetch`/AJAX
- **Camada de Aplicação (Backend / API REST)**
  - Controllers Spring Boot expondo os endpoints `/api/...`
  - Serviços contendo as regras de negócio
  - Repositórios abstraindo o acesso ao MongoDB
- **Camada de Persistência (Banco de Dados)**
  - MongoDB (coleções: `tutores`, `hospitais`, `animais`, `logs_emergencia`, etc.)

---

## 6. Tecnologias Utilizadas

- **Linguagem:** Java 17
- **Framework Backend:** Spring Boot 3.x
  - Spring Web
  - Spring Data MongoDB
  - Spring Security
- **Banco de Dados:** MongoDB (MongoDB Atlas / cluster na nuvem)
- **Frontend:**
  - HTML5, CSS3
  - JavaScript (ES6+)
  - jQuery
  - Bootstrap / Tailwind CSS (para layout e responsividade)
- **Autenticação:** JWT
- **Criptografia de senha:** BCrypt
- **Ferramentas adicionais:**
  - Maven (gerenciador de dependências)
  - Git/GitHub (versionamento de código)
  - Render (hospedagem do backend – ex.: `https://petone-pfc.onrender.com/`)

---

## 7. Endpoints da API (Exemplos)

> **Base URL (produção):** `https://petone-pfc.onrender.com/api`  
> **Base URL (desenvolvimento):** `http://localhost:8080/api`

### 7.1 Autenticação (`/api/auth`)

- `POST /auth/login`
  - Request: `{ "email": "...", "senha": "..." }`
  - Response: `{ "token": "JWT..." }`
- `POST /auth/register/tutor`
- `POST /auth/register/hospital`
- `POST /auth/forgot-password`
- `POST /auth/reset-password`

### 7.2 Tutor (`/api/tutores`)

- `GET /tutores/me`  
  Retorna os dados do tutor logado.
- `PUT /tutores/me`  
  Atualiza dados do tutor autenticado.

### 7.3 Hospital (`/api/hospitais`)

- `GET /hospitais/me`
- `PUT /hospitais/me`
- (Opcional) `GET /hospitais` – listagem para fins administrativos.

### 7.4 Animal (`/api/animais`)

- `GET /animais` – lista animais do tutor logado.
- `POST /animais` – cria um novo animal.
- `PUT /animais/{id}` – atualiza animal.
- `DELETE /animais/{id}` – remove animal.

### 7.5 Emergência (`/api/emergencias`)

- `POST /emergencias/iniciar`
  - Recebe dados da emergência e do animal selecionado.
  - Aciona a lógica de encontrar hospital parceiro.
  - Gera `tokenEmergencia`.
- `GET /emergencias/{token}` (opcional)  
  - Consulta st
