<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
  <link rel="stylesheet" href="/css/strongfellow.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Transaction ${model.transactionHash}</title>
</head>
<body>
<div id="body" class="pure-g">
<div class="pure-u-1-3">
<div class="content-container">
  <table>
    <thead><tr><th>Summary</th></tr></thead>
    <tbody>
      <tr><td class="key">Size (bytes)</td><td class="value">${model.transactionSummary.size}</td></tr>
      <tr><td class="key">Output Value</td><td class="value">${model.transactionSummary.outputValue}</td></tr>
    </tbody>
  </table>
</div>
</div><!-- end pure-u-1-3 -->

<div class="pure-u-1-1">
<div class="pure-g">
  <div class="pure-u-5-12">
  <div class="content-container">
  <#list model.transactionSummary.inputs as input>
  </#list>
  </div>
  </div>
  <div class="pure-u-2-12">
  <div class="content-container">
  </div>
  </div>
  <div class="pure-u-5-12">
  <#list model.transactionSummary.outputs as output>
  </#list>
  <div class="content-container">
  </div>
  </div>
</div>
</div><!-- end pure-u-1-1 -->

</div><!-- end pure-g -->
</body>
</html>
