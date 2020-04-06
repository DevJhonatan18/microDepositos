#!/bin/bash

curl -X POST "localhost:$1/v1/depositEvent" -H 'Content-Type:application/json' --data '{"id":12345 , "amount": 123 , "type":"INFO" , "creationDate": "123" , "account": 123}'

