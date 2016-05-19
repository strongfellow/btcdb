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
      <tr>
        <td>Number of Transactions</td><td>${model.numTx}</td>
      </tr>
      <tr>
        <td>Output Total</td><td></td>
      </tr>
      <tr>
        <td>Estimated Transaction Volume</td><td></td>
      </tr>
      <tr>
        <td>Transaction Fees</td><td></td>
      </tr>
      <tr>
        <td>Height</td><td>${(model.height)!"unknown"}</td>
      </tr>
      <tr>
        <td>Timestamp</td><td></td>
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
        <td>Block Reward</td><td></td>       
      </tr>
    </tbody>
  </table>
</div>
<div class="pure-u-1-3">
</div>
</body>
</html>
