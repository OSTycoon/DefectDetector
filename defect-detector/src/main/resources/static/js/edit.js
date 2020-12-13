$(function () {
    
  $("#defectType").selectize();
  $("#editReportedDefect").click(function() {
    editReportedDefect();
  })
  $("#editDefect")

  })

function editReportedDefect() {
    var reportedDefect = new Object();
    reportedDefect.milepost = $("#milepost").val();
    reportedDefect.description = $("#description").val();
    reportedDefect.dateReported = $("#dateReported").val();
    reportedDefect.dateRepaired = $("#dateRepaired").val(); 
    reportedDefect.defectType = $("#defectType").val();
    reportedDefect.defectId = $("#defectId").val();
    reportedDefect.rdId = $("#rdId").val();
    $.ajax({
      url: "/editReportedDefect",
      data: reportedDefect,
      type: "POST",
      success: function(returnData) {
        alert("Updated");
        location.reload();
      }
    });
  }