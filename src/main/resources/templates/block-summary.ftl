<!DOCTYPE html>
<html lang="en">
<head>
  <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Strongfellow - Block ${model.blockHash}</title>
</head>
<body>
<div id = "body" class="pure-g">
<div class="pure-u-1-3">
  <table>
    <thead><tr><th></th></tr></thead>
    <tbody>
      <tr><td>Number of Transactions</td><td>${model.numTx}</td></tr>
      <tr><td>Coinbase</td><td>${model.coinbase}</td></tr>
      <tr><td>Fees (Claimed/Available)</td><td>${model.feesClaimed}/${model.feesAvailable}</td></tr>
      <tr><td>Output Total</td><td>${model.sumOfTxOuts}</td></tr>
      <tr><td>Block Reward</td><td>${model.reward}</td></tr>
      <tr>
        <td>Estimated Transaction Volume</td><td></td>
      </tr>
      <tr>
        <td>Height</td><td>${(model.height)!"unknown"}</td>
      </tr>
      <tr>
        <td>Timestamp</td><td>${model.timestamp}</td>
      </tr>
      <tr>
        <td>Difficulty</td><td></td>
      </tr>
      <tr>
        <td>Bits</td><td></td>
      </tr>
      <tr>
        <td>Size</td><td></td>
      </tr>
      <tr>
        <td>Version</td><td></td>
      </tr>
      <tr>
        <td>Nonce</td><td></td>
      </tr>
      <tr>
        <td>Block Reward</td><td>${model.blockReward!"unknown"}</td>       
      </tr>
    </tbody>
  </table>
</div>
<div class="pure-u-1-3">
</div>
</body>
</html>
