// src/main/resources/static/app.js

const API_BASE = '/api';

// Carica e visualizza tutti i conti
enableLoadAccounts();
async function loadAccounts() {
  const res = await fetch(`${API_BASE}/accounts`);
  const accounts = await res.json();
  const listEl = document.getElementById('accounts');
  listEl.innerHTML = '';

  accounts.forEach(acc => {
    const li = document.createElement('li');
    li.classList.add('list-item');
    li.innerHTML = `
      <span>#${acc.id}</span>
      <span>${acc.ownerName}</span>
      <span>€${acc.balance.toFixed(2)}</span>
      <button class="btn-delete" data-id="${acc.id}">Elimina</button>
    `;
    listEl.appendChild(li);
  });

  // aggiungi event listener ai bottoni di delete
  document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.addEventListener('click', async () => {
      const id = btn.getAttribute('data-id');
      await fetch(`${API_BASE}/accounts/${id}`, { method: 'DELETE' });
      loadAccounts();
      loadTransactions();
    });
  });
}

// Carica e visualizza tutte le transazioni
async function loadTransactions() {
  const res = await fetch(`${API_BASE}/transactions`);
  const txs = await res.json();
  const tbody = document.getElementById('transactions-body');
  tbody.innerHTML = '';

  txs.forEach(tx => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${tx.id}</td>
      <td>${tx.account.id}</td>
      <td>€${tx.amount.toFixed(2)}</td>
      <td>${tx.description}</td>
      <td>${new Date(tx.timestamp).toLocaleString()}</td>
    `;
    tbody.appendChild(tr);
  });
}

// Inizializza form e caricamenti
function enableLoadAccounts() {
  loadAccounts();
  loadTransactions();

  // Submit creare conto
  document.getElementById('create-account-form').addEventListener('submit', async e => {
    e.preventDefault();
    const owner = document.getElementById('account-owner').value;
    const balance = parseFloat(document.getElementById('account-balance').value);
    await fetch(`${API_BASE}/accounts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ownerName: owner, balance })
    });
    loadAccounts();
  });

  // Submit creare transazione
  document.getElementById('create-transaction-form').addEventListener('submit', async e => {
    e.preventDefault();
    const accountId = parseInt(document.getElementById('tx-account-id').value);
    const amount = parseFloat(document.getElementById('tx-amount').value);
    const description = document.getElementById('tx-desc').value;

    await fetch(`${API_BASE}/transactions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        timestamp: new Date().toISOString(),
        amount,
        description,
        account: { id: accountId }
      })
    });

    loadTransactions();
    loadAccounts(); // aggiorna bilanci
  });
}