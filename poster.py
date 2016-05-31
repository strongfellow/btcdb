
import requests
import sys
import logging

def _read_exactly(n):
  result = b''
  while n != 0:
    bs = sys.stdin.buffer.read(n)
    n -= len(bs)
    result += bs
  return result

def main():
  s = requests.Session()
  logging.basicConfig(level=logging.INFO,
		      format='%(asctime)s %(message)s')
#  logging.getLogger("requests").setLevel(logging.WARNING)

  i = 0
  statuses = {}
  while True:
    magic = _read_exactly(4)
    size = sum([((x & 0xff) << (i * 8)) for i, x in enumerate(_read_exactly(4))])
    block = _read_exactly(size)
    if i < 142000:
      statuses[0] = 1 + statuses.get(0, 0)
    else:
      response = s.post('http://localhost:8080/internal/blocks', data=block,
                        headers={ 'Content-Type': 'strongfellow/block'})
      status = response.status_code
      statuses[status] = 1 + statuses.get(status, 0)
      if status != 200:
        logging.info('failed: ' + response.text)
    i += 1
    if i % 500 == 0:
      logging.info('================')
      for k,v in statuses.items():
        logging.info('%d => %d', k, v)

if __name__ == "__main__":
  main()
