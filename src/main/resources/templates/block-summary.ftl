

<#macro addressLink address>
<span class="address">
  <a href="/api/address/${address}/summary.html">${address}</a>
</span>
</#macro>

<#macro transactionLink transactionSummary>
<span class="hash">
  <a href="/api/transaction/${transactionSummary.hash}/summary.html">
  ${transactionSummary.hash}
  </a>
</span>
</#macro>

<#macro transactionDetails transaction index block>
  <@transactionLink transactionSummary = transaction />
<section id="transaction-details">
<div class="pure-g">
  <div class="pure-u-5-12">
    <div class="txins content-container">
      <table>
        <thead>
          <tr><th colspan="2">Inputs</th></tr>
          <#if transaction.inputs?has_content >
            <tr><th>Address</th><th>Value</th></tr>
          <#else>
            <tr><th>Source</th><th>Value</th></tr>
          </#if>
        </thead>
        <tbody>
        <#if transaction.inputs?has_content>
        <#list transaction.inputs as input>
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
          <tr><td>Block Reward</td><td><span class="satoshis">${block.reward}</span></td></tr>
          <tr><td>Fees</td><td><span class="satoshis">${block.feesAvailable}</span></td></tr>
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
    <#list transaction.outputs as output>
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
      <#if index == 0>
      <tr>
        <td>Unclaimed Fees</td>
        <td><span class="satoshis">${block.feesAvailable - block.feesClaimed}</span></td>
      </tr>
      </#if>
    </tbody>
    <#if transaction.fees gt 0>
    <thead>
      <tr><th colspan="3">Fees</th></tr>
    </thead>
    <tbody>
      <td></td>
      <td><span class="satoshis">${transaction.fees}</span></td>
      <td></td>
    </tbody>
    </#if>
    </table>
  </div><!-- end txouts -->
  </div><!-- end pure-u-5-12 -->
</div><!-- end pure-g -->
</section>

</#macro>

<#macro blockLink hash>
<span class="hash">
<a href="/api/block/${hash}/summary.html">${hash}</a>
</#macro>

<#macro merkleLink merkle>
<span class="hash">
  <a href="/api/merkle/${merkle}/summary.html">${merkle}</a>
</#macro>

<#macro summaryTable model>
<div class="content-container">
<section>
  <h2>Summary</h2>
  <table>
    <tbody>
      <tr><td>Number of Transactions</td><td>${model.blockSummary.numTx}</td></tr>
      <tr><td>Coinbase</td><td>${model.blockSummary.coinbaseValue}</td></tr>
      <tr><td>Fees (Claimed/Available)</td><td>${model.blockSummary.feesClaimed}/${model.blockSummary.feesAvailable}</td></tr>
      <tr><td>Output Total</td><td>${model.blockSummary.sumOfTxouts}</td></tr>
      <tr><td>Block Reward</td><td>${model.blockSummary.reward}</td></tr>
      <tr>
        <td>Estimated Transaction Volume</td><td></td>
      </tr>
      <tr>
        <td>Height</td><td>${(model.blockSummary.height)!"unknown"}</td>
      </tr>
      <tr>
        <td class="key">Depth</td>
        <td class="value">${model.blockSummary.depth}</td>
      </tr>
      <tr>
        <td>Timestamp</td><td>${model.blockSummary.timestamp}</td>
      </tr>
      <tr>
        <td class="key">Difficulty</td><td class="value"></td>
      </tr>
      <tr>
        <td>Bits</td><td>${model.blockSummary.bits}</td>
      </tr>
      <tr>
        <td>Size</td><td>${model.blockSummary.size}</td>
      </tr>
      <tr>
        <td>Version</td><td>${model.blockSummary.version}</td>
      </tr>
      <tr>
        <td>Nonce</td><td>${model.blockSummary.nonce}</td>
      </tr>
      <tr>
        <td>Coinbase Script</td><td>${model.blockSummary.coinbaseScript}</td>
      </tr>
    </tbody>
  </table>
</section>
</div>
</#macro>

<#macro hashesTable model>
<div class="content-container">
<section>
  <h2>Hashes</h2>
  <table>
    <tbody>
      <tr>
        <td class="key">Block Hash</td>
        <td class="value"><@blockLink hash=model.blockSummary.hash /></td>
      </tr>
      <tr>
        <td class="key">Merkle Root</td>
        <td class="value">
          <@merkleLink merkle=model.blockSummary.merkle />
        </td>
      </tr>
      <tr>
        <td class="key">Previous Block</td>
        <td class="value"><@blockLink hash=model.blockSummary.parent /></td></tr>
      <tr>
        <td class="key">Next Blocks</td>
        <td class="value">
          <ul style="list-style-type:none; padding:0">
          <#list model.blockSummary.children as child>
            <li><@blockLink hash=child /></li>
          </#list>
          </ul>
        </td>
      </tr>
      <tr>
        <td class="key">Tip</td>
        <td class="value"><@blockLink hash=model.blockSummary.tip /></td>
      </tr>  
    </tbody>
  </table>
</section>
</div>
</#macro>

<#macro transactionsTable model>
<div class="content-container">
</section>
  <h2>Transactions</h2>
  <ol>
  <#list model.blockSummary.transactions as transaction>
    <li>
      <@transactionDetails transaction=transaction
                           index=transaction?index
                           block=model.blockSummary />
    </li>
  </#list>
  </ol>
</section>
</div>
</#macro>

<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="/css/pure-release-0.6.0/pure-min.css">
  <link rel="stylesheet" href="/css/strongfellow.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Block ${model.blockSummary.hash}</title>
</head>
<body>
<div class="pure-g">
  <div class="pure-u-1-1">
    <div class="content-container">
      <h3><@blockLink hash=model.blockSummary.hash /></h3>
    </div>
  </div>
  <div class="pure-u-1-3">
    <@summaryTable model=model />
  </div>
  <div class="pure-u-1-3">
    <@hashesTable model=model />
  </div>
</div>
<div class="pure-g">
  <div class="pure-u">
    <@transactionsTable model=model />
  </div>
</div>
</body>
</html>
