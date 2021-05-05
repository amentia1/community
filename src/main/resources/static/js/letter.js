$(function(){
	$("#sendBtn").click(send_letter);
	$("#delBtn").click(delete_msg);
	$("#notBtn").click(delete_not);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var toContent = $("#message-text").val();
	// 发送ajax
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName, "toContent":toContent},
		function (data) {
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 重新刷新
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();

			}, 2000);
		}
	);

}

function delete_msg() {
	var msg = "真的要删除吗？";
	if(confirm(msg) == true) {
		var id = $("#letterId").val();
		var conversationId = $("#conversationId").val();
		// 发送ajax
		$.post(
			CONTEXT_PATH + "/letter/delete",
			{"id":id, "conversationId":conversationId},
			function (data) {
				data = $.parseJSON(data);
				$("#hiBody").text(data.msg);
				// 显示提示框
				$("#hiModel").modal("show");
				// 重新刷新
				setTimeout(function(){
					$("#hiModel").modal("hide");
					location.reload();

				}, 2000);
			}
		);
		$(this).parents(".media").remove();
		return true;
	} else {
		return false;
	}
}

function delete_not() {
	var msg = "真的要删除吗？";
	if(confirm(msg) == true) {
		var id = $("#noticeId").val();
		// 发送ajax
		$.post(
			CONTEXT_PATH + "/notice/delete",
			{"id":id},
			function (data) {
				data = $.parseJSON(data);
				// $("#hiBody").text(data.msg);
				// // 显示提示框
				// $("#hiModel").modal("show");
				// // 重新刷新
				// setTimeout(function(){
				// 	$("#hiModel").modal("hide");
				// 	location.reload();
				//
				// }, 2000);
			}
		);
		$(this).parents(".media").remove();
		return true;
	} else {
		return false;
	}
}