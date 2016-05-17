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
  var thead = $("<thead>").append(
    $("<tr>").append(
      $("<th>").attr("colspan", 2).html("Summary")
    )
  );
  var tbody = $("<tbody>");
  tbody.append(tr("Number of Transactions", data.numTx));
  tbody.append(tr("Output Total", null));
  tbody.append(tr("Estimated Transaction Volume", null));
  tbody.append(tr("Transaction Fees", null));
  tbody.append(tr("Height", null));  
  tbody.append(tr("Timestamp", timestamp(data.timestamp)));
  tbody.append(tr("Difficulty", null));
  tbody.append(tr("Bits", null));
  tbody.append(tr("Size", data.size));
  tbody.append(tr("Version", data.version));
  tbody.append(tr("Nonce", data.nonce));
  tbody.append(tr("Block Reward", null));
  
  return $("<table>").append(thead).append(tbody);
}

function hashesTable(data) {
  var thead = $("<thead>").append(
    $("<tr>").append(
      $("<th>").attr("colspan", 2).html("Hashes")
    )
  );
  var tbody = $("<tbody>");
  tbody.append(tr("Hash", data.hash));
  tbody.append(tr("Previous Block", data.parent));
  tbody.append(tr("Next Block(s)", data.children.join(", ")));
  tbody.append(tr("Merkle Root", data.merkle));
  return $("<table>").append(thead).append(tbody);
}

function loadBlock(hash) {
  console.log("loading block " + hash);
  $.get("/blocks/" + hash + "/summary", function(response) {
    response.hash = hash;
    console.log(response);
    var summaryTable = blockSummaryTable(response);
    var ht = hashesTable(response);
    $("body").append(
      $("<div>").addClass("pure-g")
        .append($("<div>").addClass("pure-u-1-3").append(summaryTable))
        .append($("<div>").addClass("pure-u-2-3").append(ht)
      )
    );
  });
}
$(function() {
  console.log("page loaded");
  hash = "000000000003ba27aa200b1cecaad478d2b00432346c3f1f3986da1afd33e506";
  loadBlock(hash); 
});
});
