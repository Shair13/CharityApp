(function () {

    const initSummaryBtn = document.querySelector('#summaryLoad');

    initSummaryBtn.addEventListener('click', function (e){
        const bagsInputValue = document.querySelector('input[name="quantity"]').value;

        const radioButtons = document.querySelectorAll('input[type="radio"][name="institution"]');
        const selectedRadioButton = Array.from(radioButtons).find(function(radioButton) {
            return radioButton.checked;
        });
        let radioButtonValue = "Nie wybrałeś organizacji";

        if (selectedRadioButton) {
            let titleElement = selectedRadioButton.parentElement.querySelector(".title");
            radioButtonValue = `Dla fundacji "${titleElement.textContent}"`;
        }

        const streetInputValue = document.querySelector('input[name="street"]').value;
        const cityInputValue = document.querySelector('input[name="city"]').value;
        const zipCodeInputValue = document.querySelector('input[name="zipCode"]').value;
        const phoneInputValue = document.querySelector('input[name="phone"]').value;
        const dateInputValue = document.querySelector('input[name="pickUpDate"]').value;
        const timeInputValue = document.querySelector('input[name="pickUpTime"]').value;
        const commentInputValue = document.querySelector('textarea[name="pickUpComment"]').value;

        const firstSpan = document.querySelector('div.summary-div ul li:first-child span:nth-child(2)');

        let bagsGramma = '';

        if(bagsInputValue === '1'){
            bagsGramma = 'worek';
        } else if (['2', '3', '4'].includes(bagsInputValue)){
            bagsGramma = 'worki';
        } else {
            bagsGramma = 'worków';
        }
        firstSpan.textContent = `${bagsInputValue} ${bagsGramma}`;

        const secondSpan = document.querySelector('div.summary-div ul li:nth-child(2) span:nth-child(2)');
        secondSpan.textContent = `${radioButtonValue}`;

        const firstUlAddress = document.querySelector('div.summary-div div.summary div:nth-child(2) ul');
        const secondUlDate = document.querySelector('div.summary-div div.summary div:nth-child(2) div:nth-child(2) ul');

        firstUlAddress.querySelector('li').textContent = streetInputValue;
        firstUlAddress.querySelector('li:nth-child(2)').textContent = cityInputValue;
        firstUlAddress.querySelector('li:nth-child(3)').textContent = zipCodeInputValue;
        firstUlAddress.querySelector('li:nth-child(4)').textContent = phoneInputValue;

        secondUlDate.querySelector('li:first-child').textContent = dateInputValue;
        secondUlDate.querySelector('li:nth-child(2)').textContent = timeInputValue;
        secondUlDate.querySelector('li:nth-child(3)').textContent = commentInputValue;
    })
})();