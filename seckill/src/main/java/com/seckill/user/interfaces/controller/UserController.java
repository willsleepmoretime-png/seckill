    package com.seckill.user.interfaces.controller;

    import com.seckill.common.result.Result;
    import com.seckill.user.interfaces.vo.UserLoginVO;
    import com.seckill.user.application.service.UserService;
    import com.seckill.user.domain.entity.User;
    import com.seckill.user.interfaces.dto.UserLoginDTO;
    import com.seckill.user.interfaces.dto.UserRegisterDTO;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/user")
    public class UserController {

        private final UserService userService;
        UserController(UserService userService){
            this.userService=userService;
        }

        @PostMapping("/register")
        public Result<Void> register(@RequestBody UserRegisterDTO dto) {
            userService.register(dto.getPhone(), dto.getPassword());
            return Result.success();
        }

        @PostMapping("/login")
        public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
            User user = userService.login(dto.getPhone(), dto.getPassword());
            UserLoginVO vo = UserLoginVO.from(user);   // ← 这个方法你来写
            return Result.success(vo);
        }
    }
