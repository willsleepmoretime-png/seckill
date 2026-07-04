

local stock=redis.call('get',KEYS[1])
if stock==false then
    return -1
end
if redis.call('sismember',KEYS[2],ARGV[1])==1 then
   return 2
end
if tonumber(stock)<=0 then
   return 0
end
redis.call('decr',KEYS[1])
redis.call('sadd',KEYS[2],ARGV[1])
return 1