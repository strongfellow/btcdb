<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
  <link rel="stylesheet" href="/css/strongfellow.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Transaction ${model.transactionHash}</title>
</head>
<body>
<div class="pure-g">
  <div class="pure-u-1-1">
  <div class="content-container">
  <a href="/api/transaction/${model.transactionHash}/summary.html">
    ${model.transactionHash}
  </a>
  </div>
  </div>
  <div class="pure-u-5-12">
  <div class="txins content-container">
    <table>
      <thead>
        <tr><th colspan="3">Inputs</th></tr>
        <#if model.transactionSummary.inputs?has_content >
          <tr><th></th><th>Address</th><th>Value</th></tr>
        </#if>
      </thead>
      <tbody>
      <#if model.transactionSummary.inputs?has_content>
      <#list model.transactionSummary.inputs as input>
        <tr>
          <td>
            <a href="/api/transaction/${input.txout}/summary.html#output${input.index}">Source</a>
          </td>
          <td><span class="address">${input.address}</span></td>
          <td><span class="satoshis">${input.value}</span></td>
        </tr>
      </#list>
      <#else>
      <tr><td>No Inputs (coinbase)</td></tr>
      </#if> 
      </tbody>
    </table>
  
  </div><!-- end txins -->
  </div>
  <div class="pure-u-1-6">
    <div class="arrow content-container"><span>&#8594;<span></div>
  </div>
  <div class="pure-u-5-12">
  <div class="txouts content-container">
    <table>
      <thead>
        <tr><th colspan="3">Outputs</th></tr>
        <tr>
          <th>Address</th>
          <th>Value</th>
          <th>Spends</th>
        </tr>        
      </thead>
    
    <tbody>
    <#list model.transactionSummary.outputs as output>
      <tr>
        <td><span class="address">${output.address}</span></td>
        <td><span class="satoshis">${output.value}</span></td>
        <td>
          <#if output.spends?has_content>
          <ul>
          <#list output.spends as spend>
            <li>
              (<a href="/api/transaction/${spend.tx}/summary.html#input${spend.index}">Spent</a>)
            </li>
          </#list>
          </ul>
          <#else>
          Unspent
          </#if>
        </td>
      </tr>
    </#list>
    </tbody>
    </table>
  </div><!-- end txouts -->
  </div>
  <div class="pure-u-1-1">
  <div class="summary content-container">
    <table>
      <thead>
        <tr><th>Summary</th></tr>
      </thead>
      <tbody>
        <tr><td class="key">Size (bytes)</td><td class="value">${model.transactionSummary.size}</td></tr>
        <tr><td class="key">Output Value</td><td class="value">${model.transactionSummary.outputValue}</td></tr>
      </tbody>
    </table>
  </div><!-- end summary -->
  </div><!-- end pure-u-1-1 -->
</div><!-- end pure-g -->
</body>
</html>
