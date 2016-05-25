<#macro addressLink address>
<span class="address">
  <a href="/api/address/${address}/summary.html">${address}</a>
</span>
</#macro>

<#macro transactionLink model>
<span class="hash">
  <a href="/api/transaction/${model.transactionSummary.hash}/summary.html">
  ${model.transactionSummary.hash}
  </a>
</span>
</#macro>

<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="/css/pure-release-0.6.0/pure-min.css">
  <link rel="stylesheet" href="/css/strongfellow.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Transaction ${model.transactionSummary.hash}</title>
</head>
<body>
<header>Transaction <@transactionLink model=model /></header>

<section id="transaction-details">
<div class="pure-g">
  <div class="pure-u-5-12">
    <div class="txins content-container">
      <table>
        <thead>
          <tr><th colspan="2">Inputs</th></tr>
          <#if model.transactionSummary.inputs?has_content >
            <tr><th>Address</th><th>Value</th></tr>
          </#if>
        </thead>
        <tbody>
        <#if model.transactionSummary.inputs?has_content>
        <#list model.transactionSummary.inputs as input>
          <tr>
            <td><@addressLink address=input.address /></td>
            <td>
              <span class="satoshis">
                <a href="/api/transaction/${input.txout}/summary.html#output${input.index}">
                ${input.value}
                </a>
              </span>
            </td>
          </tr>
        </#list>
        <#else>
          <tr><td>No Inputs (coinbase)</td></tr>
        </#if> 
        </tbody>
      </table>  
    </div><!-- end txins -->
  </div><!-- end pure-u-5-12 -->
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
        <td><@addressLink address=output.address /></td>
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
  </div><!-- end pure-u-5-12 -->
</div><!-- end pure-g -->
</section>
<hr />
<section id="transaction-summary">
<div class="pure-g">
  <div class="pure-u-1-1">
    <div class="summary content-container">
      <table>
        <thead>
          <tr><th>Summary</th></tr>
        </thead>
        <tbody>
          <tr><td class="key">Size (bytes)</td><td class="value">${model.transactionSummary.size}</td></tr>
          <tr><td class="key">Output Value</td><td class="value">${model.transactionSummary.outputValue}</td></tr>
          <tr>
            <td class="key">Block</td>
            <td class="value">
              <ul>
              <#list model.transactionSummary.blockPointers as blockPointer>
                <li>
                  <a href="/api/block/${blockPointer.hash}/summary.html">${blockPointer.hash}</a>&nbsp;(${blockPointer.height})
                </li>
              </#list>
              </ul>
            </td>
          </tr>
        </tbody>
      </table>
    </div><!-- end summary -->
  </div><!-- end pure-u-1-1 -->
</div><!-- end pure-g -->
</section>  

</body>
</html>
