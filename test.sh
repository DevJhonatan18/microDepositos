#!/bin/bash

curl -X POST "$1:$2/v1/depositEvent" -H 'Content-Type:application/json' --data '{"id":1 , "amount": 123 , "type":"2" , "creationDate": "123" , "accountId": 123}'

