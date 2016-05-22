<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
  <link rel="stylesheet" href="/css/strongfellow.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Transaction ${model.transactionHash}</title>
</head>
<body>
  <a href="/api/transaction/${model.transactionHash}/summary.html">
    ${model.transactionHash}
  </a>
  <div class="txins">
  <table>
  <tbody>
  <#list model.transactionSummary.inputs as input>
    <tr>
      <td>
        <a href="/api/transaction/${input.txout}/summary.html#${input.index}">Source</a>
      </td>
      <td>${input.address}</td>
      <td>${input.value}</td>
    </tr>
  </#list>
  </tbody>
  </table>
  <div class="arrow"><span>&#8594;<span></div>
  <div class="txouts">
    <ul>
    <#list model.transactionSummary.outputs as output>
      <li><span>${output.address}</span><span>${output.value}</span></li>
    </#list>
    </ul>
  </div>
  <div>
    <table>
      <thead>
        <tr><th>Summary</th></tr>
      </thead>
      <tbody>
        <tr><td class="key">Size (bytes)</td><td class="value">${model.transactionSummary.size}</td></tr>
        <tr><td class="key">Output Value</td><td class="value">${model.transactionSummary.outputValue}</td></tr>
      </tbody>
    </table>
  </div>
</body>
</html>
