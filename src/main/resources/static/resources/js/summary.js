(function () {

    const initSummaryBtn = document.querySelector('#summaryLoad');

    initSummaryBtn.addEventListener('click', function (e) {

        const bagsInputValue = getQuerySelector('input', 'quantity');
        const checkboxes = document.querySelectorAll('div.form-group--checkbox input[type="checkbox"]');

        document.querySelector('div.summary-div ul li:first-child span:nth-child(2)').textContent = `${bagsInputValue} ${bagsGramma(bagsInputValue)} (${isChecked(checkboxes)})`;
        document.querySelector('div.summary-div ul li:nth-child(2) span:nth-child(2)').textContent = getCheckedRadioBtnValue('institution');

        const firstUlAddress = document.querySelector('div.summary-div div.summary div:nth-child(2) ul');
        const secondUlDate = document.querySelector('div.summary-div div.summary div:nth-child(2) div:nth-child(2) ul');

        firstUlAddress.querySelector('li').textContent = getQuerySelector('input', 'street');
        firstUlAddress.querySelector('li:nth-child(2)').textContent = getQuerySelector('input', 'city');
        firstUlAddress.querySelector('li:nth-child(3)').textContent = getQuerySelector('input', 'zipCode');
        firstUlAddress.querySelector('li:nth-child(4)').textContent = getQuerySelector('input', 'phone');

        secondUlDate.querySelector('li').textContent = getQuerySelector('input', 'pickUpDate');
        secondUlDate.querySelector('li:nth-child(2)').textContent = getQuerySelector('input', 'pickUpTime');
        secondUlDate.querySelector('li:nth-child(3)').textContent = getQuerySelector('textarea', 'pickUpComment');
    })

    const getQuerySelector = function (type, name) {
        const selector = type + '[name="' + name + '"]';
        return document.querySelector(selector).value;
    }

    const bagsGramma = function (qty) {
        if (qty === '1') {
            return 'worek';
        } else if (['2', '3', '4'].includes(qty)) {
            return 'worki';
        }
        return 'worków'
    }

    const getCheckedRadioBtnValue = function (name) {
        let radioButtonValue = "Nie wybrałeś organizacji";
        let selector = 'input[type="radio"][name="' + name + '"]';

        const radioButtons = document.querySelectorAll(selector);
        const selectedRadioButton = Array.from(radioButtons).find(function (radioButton) {
            return radioButton.checked;
        });

        if (selectedRadioButton) {
            let titleElement = selectedRadioButton.parentElement.querySelector(".title");
            let descriptionElement = titleElement.nextElementSibling;
            radioButtonValue = `Dla fundacji "${titleElement.textContent}" - ${descriptionElement.textContent}`;
        }

        return radioButtonValue;
    }

    function isChecked(obcjectList) {
        let checked = [];
        obcjectList.forEach(function (object) {
            if (object.checked) {
                checked.push(object.parentElement.children[2].textContent)
            }
        })
        return checked.join(", ");
    }

})();