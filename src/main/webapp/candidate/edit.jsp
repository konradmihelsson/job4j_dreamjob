<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.job4j.dream.model.Candidate" %>
<%@ page import="ru.job4j.dream.store.PsqlStore" %>
<%@ page import="ru.job4j.dream.model.City" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
            crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script>
        (function () {
            'use strict';
            window.addEventListener('load', function () {
                // Fetch all the forms we want to apply custom Bootstrap validation styles to
                var forms = document.getElementsByClassName('needs-validation');
                // Loop over them and prevent submission
                var validation = Array.prototype.filter.call(forms, function (form) {
                    form.addEventListener('submit', function (event) {
                        if (form.checkValidity() === false) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
            }, false);
        })();
    </script>
    <script>
        $(document).ready(function () {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8081/dreamjob/city',
                dataType: 'json'
            }).done(function (data) {
                for (let cityName of data) {
                    $('#candidateCity').append('<option>' + cityName + '</option>')
                }
            }).fail(function (err) {
                console.log(err);
            });
        });
    </script>

    <title>???????????? ??????????</title>
</head>
<body>
<%
    String id = request.getParameter("id");
    Candidate candidate = new Candidate(0, "", new City(0, ""));
    if (id != null) {
        candidate = PsqlStore.instOf().findCandidateById(Integer.parseInt(id));
    }
%>
<div class="container pt-3">
    <div class="row">
        <ul class="nav">
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/index.jsp">??????????????</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/posts.do">????????????????</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/candidates.do">??????????????????</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/post/edit.jsp">???????????????? ????????????????</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/candidate/edit.jsp">???????????????? ??????????????????</a>
            </li>
            <c:choose>
                <c:when test="${user != null}">
                    <li class="nav-item">
                        <a class="nav-link" href="<%=request.getContextPath()%>/logout.do"> <c:out
                                value="${user.name}"/> | ??????????</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="nav-item">
                        <a class="nav-link" href="<%=request.getContextPath()%>/login.jsp">??????????</a>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">
                <% if (id == null) { %>
                ?????????? ????????????????.
                <% } else { %>
                ???????????????????????????? ???????????? ??????????????????.
                <% } %>
            </div>
            <div class="card-body">
                <form class="needs-validation"
                      action="<%=request.getContextPath()%>/candidates.do?id=<%=candidate.getId()%>" method="post"
                      novalidate>
                    <div class="form-group">
                        <label>??????:
                            <input type="text" class="form-control" name="candidateName"
                                   value="<%=candidate.getName()%>" required>
                            <div class="invalid-feedback">
                                ?????????????? ?????? ??????????????????.
                            </div>
                        </label>
                    </div>
                    <div class="form-group">
                        <label>??????????:
                            <select class="form-control" id="candidateCity" name="candidateCity" required>
                                <option disabled selected>???????????????? ??????????</option>
                            </select>
                            <div class="invalid-feedback">
                                ???????????????? ??????????.
                            </div>
                        </label>
                    </div>
                    <button type="submit" class="btn btn-primary">??????????????????</button>
                </form>
                <% if (id != null) { %>
                <form action="<%=request.getContextPath()%>/candidate_remove.do?id=<%=candidate.getId()%>"
                      method="post">
                    <button type="submit" class="btn btn-warning">??????????????</button>
                </form>
                <% } %>
            </div>
        </div>
    </div>
</div>
</body>
</html>
