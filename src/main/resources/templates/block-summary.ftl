<#macro blockLink hash>
<span class="hash">
<a href="/api/block/${hash}/summary.html">${hash}</a>
</#macro>

<#macro merkleLink merkle>
<span class="hash">
  <a href="/api/merkle/${merkle}/summary.html">${merkle}</a>
</#macro>

<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="/css/pure-release-0.6.0/pure-min.css">
  <link rel="stylesheet" href="/css/strongfellow.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Block ${model.blockHash}</title>
</head>
<body>
<div id = "body" class="pure-g">
<div class="pure-u-1-3">
<div class="content-container">
  <table>
    <thead><tr><th>Summary</th></tr></thead>
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
        <td class="key">Tip</td>
        <td class="value"><@blockLink hash=model.blockSummary.tip /></td>
      </tr>
      <tr>
        <td class="key">Depth</td>
        <td class="value">${model.blockSummary.depth}</td>
      </tr>
      <tr>
        <td>Timestamp</td><td>${model.blockSummary.timestamp}</td>
      </tr>
      <tr>
        <td>Difficulty</td><td></td>
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
</div>
</div>
<div class="pure-u-1-3">
<div class="content-container">
  <table>
  <thead>
    <tr><th>Hashes</th></tr>
  </thead>
  <tbody>
    <tr>
      <td class="key">Block Hash</td>
      <td class="value"><@blockLink hash=model.blockHash /></td>
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
  </tbody>
  </table>
</div>
</div>
</body>
</html>
