$(function () {
    $("#uploadForm").submit(upload);
})

function upload() {

    // 不能写$.post，因为有些参数不能设置，所以得用原先的
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        method: "post",
        processData: false, // 不要把表单的内容转为字符串（默认会）
        // 不要让jquery设置上传的类型，让浏览器自动设置。主要是边界问题，
        // 提交文件是二进制的，在和其他数据混合在一起时，边界如何确定浏览器会给它加一个字符串好划出边界；
        // 而jquery就不会设置边界，所以可能会有问题
        contentType: false,
        // 传的数据，传文件需要用FormData。$("#uploadForm")这个是jquery对象，这里需要js对象。因为jquery对象本质是dom的数组，所以取一个就是js对象
        data: new FormData($("#uploadForm")[0]),
        // 七牛云会返回一个JSON的data
        success: function (data) {
            if(data && data.code == 200) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if(data.code == 200) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                )
            } else {
                if(data.msg == null) {
                    alert("上传失败！");
                } else {
                    alert(data.msg);
                }
            }
        }
    });


    // 原先会尝试提交setting的表单，但是那个表单没有写eaction，就会出错
    // 所以意思是：上面的已经把头像提交了，下面就不用执行页面的表单
    return false;

}