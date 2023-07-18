 $('#mail-Check-Btn').click(function() {
		const email = $('#userEmail1').val();; // 이메일 주소값 얻어오기!
		console.log('완성된 이메일 : ' + email); // 이메일 오는지 확인
		const checkInput = $('.mail-check-input') // 인증번호 입력하는곳
		const checkEmail = $('.checkedEmail') // 인증이메일 확인용
		$('#emailConfirm').prop('checked', false);
	    const confirm = $('#emailConfirm').val();

		$.ajax({
			type : 'post',
            url : '/login/mail/confirm',
            data : 'email='+email,
			success : function (data) {
				console.log("data : " +  data);
				checkInput.attr('disabled',false);
				document.getElementById("checkedEmail").value=email;
				code =data;
				alert('인증번호가 전송되었습니다.')
			}
		}); // end ajax
	}); // end send email

	// 인증번호 비교
	// blur -> focus가 벗어나는 경우 발생
	$('.mail-check-input').blur(function () {
		const inputCode = $(this).val();
		const $resultMsg = $('#mail-check-warn');

		if(inputCode === code){
			$resultMsg.html('인증번호가 일치합니다. 본인인증 되셨습니다!.');
			$resultMsg.css('color','green');
			$('#mail-Check-Btn').attr('disabled',true);
			$('#userEmail1').attr('readonly',true);
			document.getElementById("emailConfirm").checked="true";
			console.log("emailConfirm : " +  confirm);
		}else{
			$resultMsg.html('인증번호가 불일치 합니다. 다시 확인해주세요!.');
			$resultMsg.css('color','red');
		}
	});
