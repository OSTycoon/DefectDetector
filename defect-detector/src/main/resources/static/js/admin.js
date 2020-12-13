$(function () {
    $("#addNewReportedDefect").click(function() {
      addReportedDefect();
    })
  
    $("#addNewDefect").click(function() {
      addDefect();
    })

    $("#addUser").click(function (){
        addUser();
    })

    
  $("#defectType").selectize();
  })


function addReportedDefect() {
    var newReportedDefect = new Object();
    newReportedDefect.milepost = $("#milepost").val();
    newReportedDefect.description = $("#description").val();
    newReportedDefect.dateReported = $("#dateReported").val();
    newReportedDefect.defectType = $("#defectType").val();
    $.ajax({
      url: "/addReportedDefect",
      data: newReportedDefect,
      type: "POST",
      success: function(returnData) {
        location.reload();
      }
    });
  }
  
  function addDefect() {
    var newDefect = new Object();
    newDefect.description = $("#description").val();
    newDefect.cfr = $("#cfr").val();
    newDefect.defectCode = $("#defectCode").val();
    newDefect.subrule = $("#subrule").val();
    newDefect.effortToRepair = $("#effortToRepair").val();
    $.ajax({
      url: "/addDefect",
      data: newDefect,
      type: "POST",
      success: function(returnData) {
        location.reload();
      }
    });
  }

  function addUser() {
    var newUser = new Object();
    newUser.name = $("#name").val();
    newUser.username = $("#username").val();
    newUser.password = $("#password").val();
    newUser.auth = $("#authority").val();
    $.ajax({
      url: "/addUser",
      data: newUser,
      type: "POST",
      success: function(returnData) {
        location.reload();
      }
    });
  }

  function deleteReportedDefect(id) {
    var delRD = new Object();
    delRD.id = id
    $.ajax({
      url: "/deleteReportedDefect",
      data: delRD,
      type: "DELETE",
      success: function(returnData) {
        alert("Deleted");
        updateTable();
      }
    });
  }