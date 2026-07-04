-- KEYS[1]=库存key  KEYS[2]=已购key  ARGV[1]=userId
if redis.call('SISMEMBER',KEYS[2],ARGV[1])==0 then
        return 0
end

redis.call('INCR', KEYS[1])
redis.call('SREM', KEYS[2], ARGV[1])
return 1