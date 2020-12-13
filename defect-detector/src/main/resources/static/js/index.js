$(function () {
  $("#filterDefectType").selectize();
  updateTable();
  formatTable();

  $("#search").click(function(){
    updateTable();
  })
})

function toggleManage()
{
  $(this.event.target).closest('tr').next('tr').toggle();
}
function formatTable() {
  $('[data-toggle="tooltip"]').tooltip();

  $('[data-effort="MARGINAL"]').addClass("bg-effort-1");
  $('[data-effort="MINIMAL"]').addClass("bg-effort-2");
  $('[data-effort="MODERATE"]').addClass("bg-effort-3");
  $('[data-effort="SIGNIFICANT"]').addClass("bg-effort-4");
  $('[data-effort="MAJOR"]').addClass("bg-effort-5");

  $(".manage-row").hide();

}

function updateTable() {
  var filter = new Object();
  filter.fromMp = $("#fromMp").val();
  filter.toMp = $("#toMp").val();
  filter.filterDefectType = $("#filterDefectType").val();
  filter.includeRepaired = $("#includeRepaired").is(":checked") ? "true" : "false";
  $.ajax({
    url: "/index-table",
    data: filter,
    type: "POST",
    success: function(returnData) {
      $("#indexTable").html(returnData)
      formatTable();
    }
  });
}

function markDefectDone(id) {
  var data = new Object();
  data.id = id;
  $.ajax({
    url: "/markDone",
    data: data,
    type: "POST",
    success: function() {
      alert("Marked Complete")
      updateTable()
    }
  });
}
