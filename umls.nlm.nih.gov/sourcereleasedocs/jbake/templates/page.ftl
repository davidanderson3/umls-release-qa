<#include "header.ftl">
  <div class="container-fluid" style="background-color:#63666a !important;">
    <div class="container">
      <#include "uts-documentation-menu.ftl">
    </div>
  </div>
  <div class="container-fluid">
      <div class="row">
        <#include "vocab-menu.ftl">
        <div class="col-lg-9 vol-xl-10">
          <#include "breadcrumbs.ftl">
            <h1>
              <#escape x as x?xml>${content.title}</#escape>
            </h1>
            ${content.body}
        </div>
      </div>
      <!--end row-->
  </div>
  <#include "footer.ftl">