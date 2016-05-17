requirejs.config({
  baseUrl: 'scripts/lib',
  paths: {
    jquery: 'jquery-2.2.3.min'
  }
});

requirejs(["jquery", "moment.min"], function($, moment) {

  function tr(key, value) {
    return $("<tr>").append($("<td>").html(key)).append($("<td>").html(value));
  }
  function timestamp(n) {
    return moment.unix(n).utc().format("YYYY-MM-DD hh:mm:ss");
  }
function blockSummaryTable(data) {
  var tbody = $("<tbody>");
  tbody.append(tr("Number of Transactions", data.numTx));
  tbody.append(tr("Timestamp", timestamp(data.timestamp)));
  return $("<table>").append(tbody);
}

function loadBlock(hash) {
  console.log("loading block " + hash);
  $.get("/blocks/" + hash + "/summary", function(response) {
    console.log(response);
    $("body").append(blockSummaryTable(response));
  });
}
$(function() {
  console.log("page loaded");
  hash = "000000000003ba27aa200b1cecaad478d2b00432346c3f1f3986da1afd33e506";
  loadBlock(hash); 
});
});
