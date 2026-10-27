'use strict';

const API = {
  wallets: '/wallets',
  payments: '/payments/allPayments',
  nodeInfo: '/bitcoin/node-info',
  walletInfo: '/bitcoin/wallet-info',
  externalTransfer: '/payments/externalTransfer',
  internalTransfer: '/payments/internalTransfer',
  paymentById: id => `/payments/getById?paymentId=${encodeURIComponent(id)}`,
  confirmation: id => `/payments/${encodeURIComponent(id)}/confirmation`
};

const state = { wallets: [], payments: [], node: null, bitcoinWallet: null, lastPaymentId: null };
const $ = id => document.getElementById(id);
const esc = value => String(value ?? '').replace(/[&<>"']/g, ch => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[ch]));
const first = (obj, keys, fallback = '') => {
  for (const key of keys) if (obj && obj[key] !== undefined && obj[key] !== null) return obj[key];
  return fallback;
};
const walletId = w => first(w, ['walletId','id']);
const walletCurrency = w => first(w, ['cryptocurrency','cryptoCurrency','currency'], 'Unknown');
const walletBalance = w => first(w, ['balance','availableBalance'], 0);
const walletAddress = w => first(w, ['walletAddress','address'], '—');
const paymentId = p => first(p, ['paymentId','id']);
const paymentAmount = p => first(p, ['amount','paymentAmount'], 0);
const paymentCurrency = p => first(p, ['cryptocurrency','cryptoCurrency','currency'], '—');
const paymentHash = p => first(p, ['transactionHash','txid','transactionId'], '');
const paymentType = p => String(first(p, ['paymentType','type'], '—')).toUpperCase();
const paymentStatus = p => String(first(p, ['status','paymentStatus'], 'UNKNOWN')).toUpperCase();
const paymentDate = p => first(p, ['createdAt','creationTime','timestamp'], '—');
const formatAmount = (v, currency = '') => {
  if (v === null || v === undefined || v === '') return '—';
  const n = Number(v);
  if (!Number.isFinite(n)) return `${v} ${currency}`.trim();
  const digits = /btc|bitcoin/i.test(currency) ? 8 : 8;
  return `${n.toLocaleString('en-US', {minimumFractionDigits: 0, maximumFractionDigits: digits})}${currency ? ` ${currency}` : ''}`;
};
const compactHash = hash => hash ? `${hash.slice(0, 9)}…${hash.slice(-7)}` : 'Not assigned';
const formatDate = value => {
  if (!value || value === '—') return '—';
  const date = Array.isArray(value) ? new Date(value[0], (value[1]||1)-1, value[2]||1, value[3]||0, value[4]||0, value[5]||0) : new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString(undefined, {month:'short', day:'numeric', year:'numeric', hour:'2-digit', minute:'2-digit'});
};
function statusClass(status) {
  if (status === 'COMPLETED' || status === 'CONFIRMED') return 'status-completed';
  if (status === 'BROADCASTED') return 'status-broadcasted';
  if (status === 'PENDING' || status === 'PROCESSING') return 'status-pending';
  if (status === 'FAILED') return 'status-failed';
  return 'status-unknown';
}
function badge(status) { return `<span class="status-badge ${statusClass(status)}">${esc(status)}</span>`; }
function toast(title, message, kind = 'success') {
  const el = document.createElement('div'); el.className = `toast ${kind}`;
  el.innerHTML = `<b>${esc(title)}</b>${esc(message)}`; $('toast-region').appendChild(el);
  setTimeout(() => el.remove(), 5500);
}
function showLoading(show, label = 'Working…') { $('loading-label').textContent = label; $('loading-overlay').classList.toggle('hidden', !show); }
async function api(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { ...(options.body ? {'Content-Type':'application/json'} : {}), ...(options.headers || {}) } });
  const text = await response.text();
  let data = null;
  if (text) { try { data = JSON.parse(text); } catch { data = text; } }
  if (!response.ok) {
    let message = typeof data === 'string' ? data : first(data, ['message','error','detail','title'], JSON.stringify(data || `HTTP ${response.status}`));
    if (data && data.errors && typeof data.errors === 'object') message += ': ' + Object.entries(data.errors).map(([k,v])=>`${k}: ${v}`).join(', ');
    throw new Error(`${response.status} ${response.statusText}: ${message}`);
  }
  return data;
}
function showPage(name) {
  document.querySelectorAll('.page-section').forEach(section => section.classList.toggle('active', section.id === `page-${name}`));
  document.querySelectorAll('.nav-item').forEach(btn => btn.classList.toggle('active', btn.dataset.page === name));
  const labels = {dashboard:'Dashboard',wallets:'Wallets',send:'Send external payment',internal:'Internal transfer',transactions:'Transaction history',network:'Bitcoin network'};
  $('page-crumb').textContent = labels[name] || 'Dashboard'; $('sidebar').classList.remove('open'); window.scrollTo({top:0,behavior:'smooth'});
  if (name === 'wallets') renderWallets(); if (name === 'transactions') renderPayments(); if (name === 'network') renderNode();
}
function navBind() { document.querySelectorAll('[data-page]').forEach(el => el.addEventListener('click', () => showPage(el.dataset.page))); }
async function refreshAll() {
  const tasks = await Promise.allSettled([loadWallets(), loadPayments(), loadNodeInfo(), loadBitcoinWalletInfo()]);
  const failed = tasks.filter(t => t.status === 'rejected');
  renderDashboard(); renderWallets(); renderPayments(); renderNode(); populateWalletSelects();
  if (failed.length) toast('Some data could not load', `${failed.length} API call(s) failed. Check that Spring Boot and Bitcoin Core are running.`, 'error');
}
async function loadWallets() { state.wallets = await api(API.wallets); if (!Array.isArray(state.wallets)) state.wallets = []; }
async function loadPayments() { state.payments = await api(API.payments); if (!Array.isArray(state.payments)) state.payments = []; }
async function loadNodeInfo() { state.node = await api(API.nodeInfo); }
async function loadBitcoinWalletInfo() { try { state.bitcoinWallet = await api(API.walletInfo); } catch (e) { state.bitcoinWallet = null; throw e; } }
function renderDashboard() {
  $('wallet-count').textContent = state.wallets.length;
  $('payment-count').textContent = state.payments.length;
  const pending = state.payments.filter(p => ['PENDING','PROCESSING','BROADCASTED'].includes(paymentStatus(p))).length;
  $('broadcasted-count').textContent = `${pending} in progress`;
  const btcWallets = state.wallets.filter(w => /bitcoin|btc/i.test(walletCurrency(w)));
  $('btc-wallet-count').textContent = `${btcWallets.length} database Bitcoin wallet(s)`;
  // Do not sum different wallet ledgers with Bitcoin Core. Show a single BTC value only when unambiguous.
  $('btc-balance').textContent = btcWallets.length === 1 ? `${formatAmount(walletBalance(btcWallets[0]), 'BTC')}` : (btcWallets.length > 1 ? `${btcWallets.length} BTC wallets` : 'No BTC wallet');
  if (state.node) {
    const online = state.node.chain === 'testnet4';
    $('node-status').textContent = online ? 'Connected' : 'Check node';
    $('node-foot').textContent = state.node.initialblockdownload ? 'Node synchronizing' : 'Node RPC responding';
    $('network-name').textContent = state.node.chain || '—'; $('block-height').textContent = Number(state.node.blocks || 0).toLocaleString();
    $('sync-progress').textContent = `${(Number(state.node.verificationprogress || 0)*100).toFixed(2)}%`;
    $('pruned-state').textContent = state.node.pruned ? 'Yes' : 'No';
    $('node-badge').className = `status-badge ${online ? 'status-completed':'status-pending'}`; $('node-badge').textContent = online ? 'RPC ONLINE':'CHECK NETWORK';
    $('sidebar-node-status').textContent = online ? (state.node.initialblockdownload ? 'Synchronizing':'RPC connected') : 'Check connection';
  } else { $('node-status').textContent='Unavailable'; $('node-foot').textContent='Unable to read node RPC'; $('sidebar-node-status').textContent='Node unavailable'; }
  const sorted = [...state.payments].sort((a,b)=>String(paymentDate(b)).localeCompare(String(paymentDate(a))));
  $('recent-transactions').innerHTML = sorted.slice(0,5).map(paymentRow).join('') || '<tr><td colspan="5" class="table-empty">No payments found yet.</td></tr>';
}
function paymentRow(p) {
  const id = paymentId(p); const type = paymentType(p); const curr = paymentCurrency(p); const amount = paymentAmount(p); const status = paymentStatus(p); const hash = paymentHash(p);
  return `<tr><td><b>#${esc(id ?? '—')}</b></td><td><span class="type-cell"><span class="type-icon ${type.includes('INTERNAL')?'internal':''}">${type.includes('INTERNAL')?'⇄':'↗'}</span>${esc(type === 'EXTERNAL' ? 'External' : type === 'INTERNAL' ? 'Internal' : type)}</span></td><td>${esc(formatAmount(amount, curr))}</td><td>${badge(status)}</td><td>${hash ? `<span class="txid" title="${esc(hash)}">${esc(compactHash(hash))}</span><button class="copy-btn" data-copy="${esc(hash)}" title="Copy transaction hash">▢</button>` : '<span class="muted">—</span>'}</td></tr>`;
}
function renderWallets() {
  const grid = $('wallet-card-grid'); const tbody = $('wallet-table');
  if (!state.wallets.length) { grid.innerHTML='<div class="empty-card">No wallets were returned by <code>GET /wallets</code>.</div>'; tbody.innerHTML='<tr><td colspan="6" class="table-empty">No wallets found.</td></tr>'; return; }
  grid.innerHTML = state.wallets.map(w => { const currency=walletCurrency(w); const bitcoin=/bitcoin|btc/i.test(currency); const address=walletAddress(w); const locked=first(w,['lockedBalance','lockedAmount'],null); return `<article class="wallet-card"><div class="wallet-card-top"><span class="coin-badge ${bitcoin?'':'other'}">${bitcoin?'₿':/eth/i.test(currency)?'◆':'◈'}</span><div><h3>${esc(currency)}</h3><small>Wallet #${esc(walletId(w))}</small></div>${w.status ? `<span class="status-badge ${String(w.status).toUpperCase()==='ACTIVE'?'status-completed':'status-unknown'}">${esc(w.status)}</span>`:''}</div><div class="wallet-balance">${esc(formatAmount(walletBalance(w), currency))}</div><small>Database wallet balance</small><div class="wallet-address" title="${esc(address)}">${esc(address)}</div><div class="wallet-card-bottom"><span>Available ledger record</span><button class="text-button" data-copy="${esc(address)}">Copy address</button></div>${locked!==null?`<div class="wallet-card-bottom"><span>Locked balance</span><b>${esc(formatAmount(locked,currency))}</b></div>`:''}</article>`; }).join('');
  tbody.innerHTML = state.wallets.map(w => `<tr><td>#${esc(walletId(w))}</td><td>${esc(walletCurrency(w))}</td><td><span class="txid" title="${esc(walletAddress(w))}">${esc(walletAddress(w))}</span><button class="copy-btn" data-copy="${esc(walletAddress(w))}">▢</button></td><td>${esc(formatAmount(walletBalance(w),walletCurrency(w)))}</td><td>${badge(String(first(w,['status'],'UNKNOWN')).toUpperCase())}</td><td>${esc(first(w,['userId','user.id','user.userId'], first(w,['userId'],'—')))}</td></tr>`).join('');
}
function renderPayments() {
  const query = ($('payment-search')?.value || '').toLowerCase().trim(); const filter = $('status-filter')?.value || 'ALL';
  const sorted = [...state.payments].sort((a,b)=>String(paymentDate(b)).localeCompare(String(paymentDate(a))));
  const filtered = sorted.filter(p => (filter==='ALL'||paymentStatus(p)===filter) && (!query || `${paymentId(p)} ${paymentHash(p)} ${paymentType(p)} ${paymentCurrency(p)}`.toLowerCase().includes(query)));
  $('payment-table-caption').textContent = `${filtered.length} payment(s) · ${state.payments.length} total`;
  $('payments-table').innerHTML = filtered.map(p => { const id=paymentId(p), hash=paymentHash(p), status=paymentStatus(p), type=paymentType(p); return `<tr><td><b>#${esc(id??'—')}</b></td><td>${esc(type)}</td><td>${esc(paymentCurrency(p))}</td><td>${esc(formatAmount(paymentAmount(p), paymentCurrency(p)))}</td><td>${badge(status)}</td><td>${esc(formatDate(paymentDate(p)))}</td><td>${hash?`<span class="txid" title="${esc(hash)}">${esc(compactHash(hash))}</span><button class="copy-btn" data-copy="${esc(hash)}">▢</button>`:'—'}</td><td><button class="action-link" data-refresh-payment="${esc(id)}">Refresh</button></td></tr>`; }).join('') || '<tr><td colspan="8" class="table-empty">No matching payments.</td></tr>';
}
function renderNode() {
  const n=state.node;
  if(!n){$('network-rpc-status').textContent='Unavailable';return;}
  $('network-rpc-status').textContent=n.chain==='testnet4'?'Connected':'Check network'; $('network-blocks').textContent=Number(n.blocks||0).toLocaleString(); $('network-headers').textContent=Number(n.headers||0).toLocaleString(); $('network-pruning').textContent=n.pruned?'Enabled':'Disabled'; $('network-prune-size').textContent=n.pruned?`${(Number(n.prune_target_size||0)/1e9).toFixed(2)} GB target`:'Full node data';
  $('detail-chain').textContent=n.chain||'—'; $('detail-best-hash').textContent=n.bestblockhash||'—'; $('detail-verification').textContent=`${(Number(n.verificationprogress||0)*100).toFixed(4)}%`; $('detail-ibd').textContent=String(n.initialblockdownload); $('detail-disk').textContent=`${(Number(n.size_on_disk||0)/1e6).toFixed(1)} MB`; $('detail-prune-height').textContent=n.pruneheight??'—'; $('network-chain-badge').className=`status-badge ${n.chain==='testnet4'?'status-completed':'status-pending'}`; $('network-chain-badge').textContent=(n.chain||'UNKNOWN').toUpperCase();
}
function populateWalletSelects() {
  const options = state.wallets.map(w=>`<option value="${esc(walletId(w))}">#${esc(walletId(w))} · ${esc(walletCurrency(w))} · ${esc(formatAmount(walletBalance(w),walletCurrency(w)))}</option>`).join('');
  for (const id of ['sender-wallet','internal-sender','internal-receiver']) { const el=$(id); if(!el) continue; const old=el.value; const placeholder=id==='internal-receiver'?'Choose receiver wallet':id==='internal-sender'?'Choose sender wallet':'Choose database sender wallet'; el.innerHTML=`<option value="">${placeholder}</option>${options}`; if(old && [...el.options].some(o=>o.value===old)) el.value=old; }
}
async function refreshPaymentRecord(id) {
  const p = await api(API.paymentById(id));
  const idx=state.payments.findIndex(existing=>String(paymentId(existing))===String(id));
  if(idx>=0) state.payments[idx]=p; else state.payments.unshift(p);
  renderDashboard(); renderPayments();
  return p;
}
async function checkConfirmation(id) {
  try {
    const result=await api(API.confirmation(id));
    const chainStatus=typeof result==='string'?result:(result?.status||JSON.stringify(result));
    toast(`Blockchain status: ${chainStatus}`, `Payment #${id} confirmation endpoint responded. Refreshing the saved payment record.`,'success');
    await refreshPaymentRecord(id);
  } catch (e) { toast('Confirmation check failed', e.message, 'error'); }
}
function bindCopy() {
  document.addEventListener('click', async e => { const btn=e.target.closest('[data-copy]'); if(!btn) return; const value=btn.dataset.copy; if(!value || value==='—') return; try { await navigator.clipboard.writeText(value); toast('Copied', 'Value copied to clipboard.'); } catch { toast('Copy unavailable','Select and copy the value manually.','error'); } });
}
function newKey() { return `CV-${Date.now()}-${Math.random().toString(36).slice(2,8).toUpperCase()}`; }
function makeIdempotency() { $('idempotency-key').value = newKey(); }
function updateSummary() { const amount=Number($('payment-amount').value||0); $('summary-amount').textContent=`${amount.toFixed(8)} BTC`; }
function bindExternalForm() {
  $('payment-amount').addEventListener('input',updateSummary); $('new-idempotency').addEventListener('click',makeIdempotency);
  $('external-payment-form').addEventListener('submit', async e => {
    e.preventDefault();
    const senderWalletId=Number($('sender-wallet').value); const amount=Number($('payment-amount').value); const receiverAddress=$('receiver-address').value.trim(); const cryptocurrency=$('payment-currency').value; const idempotencyKey=$('idempotency-key').value.trim();
    if(!senderWalletId){toast('Select a sender wallet','Choose a database wallet record first.','error');return;}
    if(!Number.isFinite(amount)||amount<=0||amount>0.0001){toast('Invalid amount','For this test integration, amount must be greater than 0 and no more than 0.0001 BTC.','error');return;}
    if(!receiverAddress.startsWith('tb1')){toast('Check the destination','The current demo expects a Bech32 Testnet4 address beginning with tb1.','error');return;}
    if(!idempotencyKey){toast('Missing idempotency key','Generate an idempotency key before submitting.','error');return;}
    const button=$('submit-payment'); button.disabled=true; button.textContent='Submitting…';
    try {
      const response=await api(API.externalTransfer,{method:'POST',body:JSON.stringify({senderWalletId,amount,cryptocurrency,externalReceiverAddress:receiverAddress,idempotencyKey})});
      const id=paymentId(response); state.lastPaymentId=id??null;
      $('payment-result').classList.remove('hidden');
      const hash=paymentHash(response); const status=paymentStatus(response);
      $('payment-result').innerHTML=`<h3>Payment request accepted</h3><p>Initial response status: <b>${esc(status)}</b>. Because processing is asynchronous, this response may precede transaction broadcasting.</p><p>Payment ID: <b>${esc(id??'Not returned')}</b></p><p>Transaction hash: <span class="result-txid">${esc(hash||'Waiting for broadcast result')}</span></p><div class="button-row"><button class="button button-outline" id="refresh-this-payment" ${id==null?'disabled':''}>Refresh payment status</button><button class="button button-quiet" id="open-transactions">Open history</button></div>`;
      $('refresh-this-payment')?.addEventListener('click',()=>pollPayment(id)); $('open-transactions')?.addEventListener('click',()=>showPage('transactions'));
      toast('Payment request created', `Payment ${id??''} was submitted. We will refresh its stored status.`, 'success');
      await Promise.allSettled([loadPayments(),loadWallets()]); renderDashboard();renderPayments();renderWallets();populateWalletSelects();
      if(id!=null) pollPayment(id);
      makeIdempotency();
    } catch(err) { toast('Payment request failed',err.message,'error'); }
    finally { button.disabled=false; button.innerHTML='<span>↗</span> Submit testnet payment'; }
  });
}
async function pollPayment(id) {
  if(id==null) return;
  const end=Date.now()+30000;
  const tick=async()=>{
    try {
      const p=await refreshPaymentRecord(id); const s=paymentStatus(p);
      if(['BROADCASTED','COMPLETED','FAILED'].includes(s)) {
        $('payment-result').classList.remove('hidden');
        $('payment-result').innerHTML=`<h3>Payment #${esc(id)} · ${esc(s)}</h3><p>Latest status retrieved from CryptoVault's payment API.</p><p>Transaction hash: <span class="result-txid">${esc(paymentHash(p)||'Not assigned')}</span></p><div class="button-row"><button class="button button-outline" id="check-payment-chain">Check blockchain status</button><button class="button button-quiet" id="view-payment-history">View history</button></div>`;
        $('check-payment-chain').addEventListener('click',()=>checkConfirmation(id)); $('view-payment-history').addEventListener('click',()=>showPage('transactions'));
        if(s==='BROADCASTED') toast('Transaction broadcasted',`Payment #${id} has a TXID and is awaiting confirmation.`,'success');
        if(s==='COMPLETED') toast('Payment completed',`Payment #${id} has been marked completed.`, 'success');
        if(s==='FAILED') toast('Payment failed',`Check payment #${id} failure details.`, 'error');
        return;
      }
    } catch (e) { /* keep polling briefly; user can inspect history manually */ }
    if(Date.now()<end) setTimeout(tick,2500);
  };
  setTimeout(tick,1200);
}
function bindInternalForm() {
  $('internal-payment-form').addEventListener('submit',async e=>{
    e.preventDefault();const senderWalletId=Number($('internal-sender').value);const receiverWalletId=Number($('internal-receiver').value);const amount=Number($('internal-amount').value);const cryptocurrency=$('internal-currency').value;const idempotencyKey=$('internal-idempotency').value.trim()||newKey();
    if(!senderWalletId||!receiverWalletId){toast('Choose both wallets','Select sender and receiver wallet records.','error');return;} if(senderWalletId===receiverWalletId){toast('Invalid transfer','Sender and receiver wallets must be different.','error');return;} if(!Number.isFinite(amount)||amount<=0){toast('Invalid amount','Enter a positive transfer amount.','error');return;}
    try { const response=await api(API.internalTransfer,{method:'POST',body:JSON.stringify({senderWalletId,receiverWalletId,amount,cryptocurrency,idempotencyKey})}); toast('Internal transfer submitted',`Payment #${paymentId(response)??'created'} returned by the API.`,'success'); await Promise.allSettled([loadPayments(),loadWallets()]);renderDashboard();renderWallets();renderPayments();populateWalletSelects();showPage('transactions'); }
    catch(err){toast('Internal transfer failed',err.message,'error');}
  });
}
function bindTableActions() {
  document.addEventListener('click', async e=>{
    const refresh=e.target.closest('[data-refresh-payment]'); if(refresh){const id=refresh.dataset.refreshPayment;refresh.disabled=true;try{const p=await refreshPaymentRecord(id);toast(`Payment #${id}`,`Current status: ${paymentStatus(p)}`,'success');}catch(err){toast('Refresh failed',err.message,'error');}finally{refresh.disabled=false;} }
  });
  $('payment-search').addEventListener('input',renderPayments); $('status-filter').addEventListener('change',renderPayments);
}
function bindRefreshButtons() {
  const all=async()=>{showLoading(true,'Refreshing CryptoVault data…');try{await refreshAll();toast('Data refreshed','Latest available API data has been loaded.');}finally{showLoading(false);}};
  $('refresh-all').addEventListener('click',all);$('footer-refresh').addEventListener('click',all);$('refresh-wallets').addEventListener('click',async()=>{try{await loadWallets();renderWallets();populateWalletSelects();toast('Wallets refreshed','Wallet records reloaded.');}catch(e){toast('Could not refresh wallets',e.message,'error');}});$('refresh-payments').addEventListener('click',async()=>{try{await loadPayments();renderDashboard();renderPayments();toast('Payments refreshed','Payment history reloaded.');}catch(e){toast('Could not refresh payments',e.message,'error');}});$('refresh-node').addEventListener('click',async()=>{try{await loadNodeInfo();renderNode();renderDashboard();toast('Node refreshed','Bitcoin Core RPC information reloaded.');}catch(e){toast('Could not reach Bitcoin Core',e.message,'error');}});$('menu-toggle').addEventListener('click',()=>$('sidebar').classList.toggle('open'));
}
async function init() {
  navBind(); bindCopy(); bindExternalForm(); bindInternalForm(); bindTableActions(); bindRefreshButtons(); makeIdempotency();
  $('today-date').textContent=new Date().toLocaleDateString(undefined,{weekday:'short',month:'short',day:'numeric'});
  await refreshAll();
  // Auto-refresh list/status data modestly. Confirmation scheduler remains server-side.
  setInterval(async()=>{try{await Promise.all([loadPayments(),loadNodeInfo()]);renderDashboard();renderPayments();renderNode();}catch{/* show explicit errors only on manual refresh */}},15000);
}
document.addEventListener('DOMContentLoaded', init);
