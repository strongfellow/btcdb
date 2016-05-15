
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
  logging.basicConfig(level=logging.INFO)
  i = 0
  while True:
    magic = _read_exactly(4)
    size = sum([((x & 0xff) << (i * 8)) for i, x in enumerate(_read_exactly(4))])
    block = _read_exactly(size)
    response = requests.post('http://localhost:8080/blocks', data=block,
                             headers={ 'Content-Type': 'strongfellow/block'})
    logging.info('%d => %s', i, response)
    i += 1

if __name__ == "__main__":
  main()	  