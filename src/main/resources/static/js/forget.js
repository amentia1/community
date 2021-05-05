function get_code() {

        // var disabled = $(".generate_code").attr("disabled");
        // if(disabled){
        //     return false;
        // }

        var email = $("#email").val();
        console.log(email);
        $.get(
            CONTEXT_PATH + "/forget/verify",
            {"email":email},
            function (data) {
                data = $.parseJSON(data);
                alert(data.msg);
            }
        )

        // var countdown=60;
        // var _generate_code = $(".generate_code");
        // function settime() {
        //     if (countdown == 0) {
        //         _generate_code.attr("disabled",false);
        //         _generate_code.val("获取验证码");
        //         countdown = 60;
        //         return false;
        //     } else {
        //         $(".generate_code").attr("disabled", true);
        //         _generate_code.val("重新发送(" + countdown + ")");
        //         countdown--;
        //     }
        //     setTimeout(function() {
        //         settime();
        //     },1000);
        // }
}







