(function(){
  function makeSortable(table){
    if(table.dataset.sortable) return;
    table.dataset.sortable = 'true';
    table.classList.add('sortable');
    const head = table.tHead;
    if(!head) return;
    head.addEventListener('click', function(ev){
      const th = ev.target.closest('th');
      if(!th) return;
      const row = th.parentNode;
      const idx = Array.prototype.indexOf.call(row.children, th);
      const tbody = table.tBodies[0];
      if(!tbody) return;
      const rows = Array.from(tbody.rows);
      const asc = th.dataset.order !== 'asc';
      row.querySelectorAll('th').forEach(cell => delete cell.dataset.order);
      th.dataset.order = asc ? 'asc' : 'desc';
      rows.sort((a,b)=>{
        const aText = a.cells[idx].textContent.trim();
        const bText = b.cells[idx].textContent.trim();
        const aNum = parseFloat(aText.replace(/[^0-9.-]/g,''));
        const bNum = parseFloat(bText.replace(/[^0-9.-]/g,''));
        if(!isNaN(aNum) && !isNaN(bNum)){
          return asc ? aNum - bNum : bNum - aNum;
        }
        return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
      });
      rows.forEach(r=>tbody.appendChild(r));
    });
  }
  function init(){
    document.querySelectorAll('table').forEach(makeSortable);
    const obs = new MutationObserver(muts=>{
      muts.forEach(m=>{
        m.addedNodes.forEach(n=>{
          if(n.nodeType!==1) return;
          if(n.tagName==='TABLE') makeSortable(n);
          else n.querySelectorAll && n.querySelectorAll('table').forEach(makeSortable);
        });
      });
    });
    obs.observe(document.body,{childList:true,subtree:true});
  }
  if(document.readyState==='loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();
