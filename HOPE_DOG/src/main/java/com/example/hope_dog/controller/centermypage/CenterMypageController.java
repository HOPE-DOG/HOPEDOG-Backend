package com.example.hope_dog.controller.centermypage;

import com.example.hope_dog.dto.centermypage.CenterProfileDTO;
import com.example.hope_dog.dto.centermypage.CenterUpdateProfileDTO;
import com.example.hope_dog.dto.centermypage.CenterViewProfileDTO;
import com.example.hope_dog.dto.centermypage.notebox.*;
import com.example.hope_dog.service.centermypage.CenterMypageService;
import com.example.hope_dog.service.centermypage.NoteBoxService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/centerMypage")
class CenterMypageController {
    private final CenterMypageService centerMypageService;
    private final HttpSession session;
    private final NoteBoxService noteBoxService;

    @GetMapping("/centerProfile")
    public String centerProfile(Model model) {
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo"); // 세션에서 센터회원 번호 가져오기

        if (centerMemberNo == null) {
            log.warn("세션에서 centerMemberNo가 존재하지 않습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        // 프로필 정보 가져오기
        CenterProfileDTO profile = centerMypageService.centerProfile(centerMemberNo);
        model.addAttribute("profile", profile); // 모델에 프로필 정보 추가

        return "centermypage/center-mypage-profile"; // HTML 파일 경로
    }

    // 업데이트 페이지 이동
    @GetMapping("/updateProfile")
    public String updateProfilePage(Model model) {
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo"); // 세션에서 센터회원 번호 가져오기

        // 프로필 정보 가져오기
        CenterViewProfileDTO profile = centerMypageService.getCenterViewProfile(centerMemberNo);
        model.addAttribute("profile", profile); // 모델에 프로필 정보 추가

        return "centermypage/center-mypage-profile-update"; // 업데이트 페이지 HTML 파일 경로
    }

    //프로필 수정
    @PostMapping("/updateProfileOk")
    public String updateProfile(@ModelAttribute CenterUpdateProfileDTO centerUpdateProfileDTO, Model model) {
        // 세션에서 centerMemberNo 가져오기
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");
        if (centerMemberNo == null) {
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }

        // DTO에 centerMemberNo 설정
        centerUpdateProfileDTO.setCenterMemberNo(centerMemberNo);

        boolean isUpdated = centerMypageService.updateCenterProfile(centerUpdateProfileDTO);

        // 현재 비밀번호가 틀린 경우 오류 메시지를 모델에 추가하고 페이지 유지
        if (!isUpdated) {
            model.addAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/centerProfile";
        }

        // 업데이트 성공 시 확인 페이지 또는 메시지 추가
        model.addAttribute("successMessage", "프로필이 성공적으로 업데이트되었습니다.");
        return "redirect:/updateProfile";
    }





    //보낸쪽지함
    @GetMapping("/noteboxSendList")
    public String sendList(Model model) {
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo"); // 세션에서 센터회원 번호 가져오기
        if (centerMemberNo == null) {
            log.error("세션에서 centerMemberNo를 찾을 수 없습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        List<NoteboxSendListDTO> noteboxSendList = noteBoxService.getSendList(centerMemberNo);
        model.addAttribute("noteboxSendList", noteboxSendList);
        log.info("SendList 요청이 들어왔습니다. centerMemberNo: {}", centerMemberNo);

        return "centermypage/notebox/center-mypage-notebox-send"; // HTML 파일 경로
    }

    //받은쪽지함
    @GetMapping("/noteboxReceiveList")
    public String receiveList(Model model) {
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo"); // 세션에서 센터회원 번호 가져오기
        if (centerMemberNo == null) {
            log.error("세션에서 centerMemberNo를 찾을 수 없습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        List<NoteboxReceiveListDTO> noteboxReceiveList = noteBoxService.getReceiveList(centerMemberNo);
        model.addAttribute("noteboxReceiveList", noteboxReceiveList);
        log.info("ReceiveList 요청이 들어왔습니다. centerMemberNo: {}", centerMemberNo);

        return "centermypage/notebox/center-mypage-notebox-receive"; // HTML 파일 경로
    }

    // 보낸 쪽지 상세 페이지
    @GetMapping("/noteboxSendDetail")
    public String getNoteboxSendDetail(@RequestParam("noteboxSendNo") Long noteboxSendNo, Model model) {
        NoteboxSendDetailDTO noteboxSendDetail = noteBoxService.getNoteboxSendDetail(noteboxSendNo);

        // 쪽지 정보를 찾지 못한 경우
        if (noteboxSendDetail == null) {
            log.error("보낸 쪽지를 찾을 수 없습니다: {}", noteboxSendNo);
            return "redirect:/centerMypage/noteboxSendList"; // 쪽지를 찾지 못하면 목록으로 리다이렉트
        }

        model.addAttribute("noteboxSendDetail", noteboxSendDetail); // 모델에 쪽지 상세 정보 추가
        return "centermypage/notebox/center-mypage-notebox-senddetail"; // 상세 페이지의 템플릿 이름
    }

    // 보낸 쪽지 상세 페이지
    @GetMapping("/noteboxReceiveDetail")
    public String getNoteboxReceiveDetail(@RequestParam("noteboxReceiveNo") Long noteboxReceiveNo, Model model) {
        NoteboxReceiveDetailDTO noteboxReceiveDetail = noteBoxService.getNoteboxReceiveDetail(noteboxReceiveNo);

        // 쪽지 정보를 찾지 못한 경우
        if (noteboxReceiveDetail == null) {
            log.error("보낸 쪽지를 찾을 수 없습니다: {}", noteboxReceiveNo);
            return "redirect:/centerMypage/noteboxReceiveList"; // 쪽지를 찾지 못하면 목록으로 리다이렉트
        }

        model.addAttribute("noteboxReceiveDetail", noteboxReceiveDetail); // 모델에 쪽지 상세 정보 추가
        return "centermypage/notebox/center-mypage-notebox-receivedetail"; // 상세 페이지의 템플릿 이름
    }


    //쪽지보내기 페이지 이동
    @GetMapping(value = "/noteboxWrite")
    public String noteboxWrite(Model model, HttpSession session) {
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo"); // 세션에서 센터회원 번호 가져오기
        if (centerMemberNo == null) {
            log.error("세션에서 centerMemberNo를 찾을 수 없습니다.");
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        // DTO 객체 생성 및 모델에 추가
        model.addAttribute("noteboxWriteDTO", new NoteboxWriteDTO());

        return "centermypage/notebox/center-mypage-notebox-write"; // HTML 파일 경로
    }

    //쪽지보내기 (답장) 페이지 이동
    @GetMapping("/noteboxResponse")
    public String writeNote(@RequestParam("noteboxSenderName") String noteboxSenderName, Model model, HttpSession session) {
        // 세션 체크 및 DTO 설정
        Long centerMemberNo = (Long) session.getAttribute("centerMemberNo");
        if (centerMemberNo == null) {
            log.error("세션에서 centerMemberNo를 찾을 수 없습니다.");
            return "redirect:/login";
        }

        NoteboxWriteDTO noteboxWriteDTO = new NoteboxWriteDTO();
        noteboxWriteDTO.setNoteboxReceiverName(noteboxSenderName); // senderName을 받는 사람 이름으로 설정
        model.addAttribute("noteboxWriteDTO", noteboxWriteDTO);

        return "centermypage/notebox/center-mypage-notebox-write";
    }

    // 쪽지 전송 실패
    @GetMapping("/notebox/sendFail")
    public String showSendNoteForm(Model model) {
        model.addAttribute("noteboxWriteDTO", new NoteboxWriteDTO());
        return "centermypage/notebox/center-mypage-notebox-write"; // 템플릿 경로
    }

    @PostMapping("/sendingNote")
    public String sendingNote(@ModelAttribute NoteboxWriteDTO noteboxWriteDTO, RedirectAttributes redirectAttributes) {
        // 세션에서 센터회원 번호 가져오기
        Long senderNo = (Long) session.getAttribute("centerMemberNo");

        if (senderNo == null) {
            redirectAttributes.addFlashAttribute("error", "발신자 정보를 찾을 수 없습니다.");
            return "redirect:/centerMypage/notebox/sendFail"; // 에러가 발생하면 다시 폼으로 돌아감
        }

        Long receiverNo;
        try {
            receiverNo = noteBoxService.findMemberNoByNickname(noteboxWriteDTO.getNoteboxReceiverName());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("수신자 조회 실패: {}", e.getMessage());
            return "redirect:/centerMypage/notebox/sendFail"; // 에러가 발생하면 다시 폼으로 돌아감
        }

        if (receiverNo != null) {
            // DTO에 수신자 번호 및 발신자 번호 설정
            noteboxWriteDTO.setNoteboxR(receiverNo);
            noteboxWriteDTO.setNoteboxS(senderNo); // 발신자 번호 설정

            // 쪽지 전송
            noteBoxService.sendingNote(noteboxWriteDTO);
            log.info("쪽지가 성공적으로 전송되었습니다: {}", noteboxWriteDTO);
            return "redirect:/centerMypage/noteboxSendList"; // 보낸 쪽지함으로 리다이렉트
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 번호를 찾을 수 없습니다.");
            log.error("회원 번호를 찾을 수 없습니다: {}", noteboxWriteDTO.getNoteboxReceiverName());
            return "redirect:/centerMypage/notebox/sendFail"; // 에러가 발생하면 다시 폼으로 돌아감
        }
    }


    @GetMapping("/noteboxDelete")
    public String deleteNote(@RequestParam("noteboxReceiveNo") Long noteboxReceiveNo, Model model) {
        boolean isDeleted = noteBoxService.deleteNoteByReceiveNo(noteboxReceiveNo);

        if (isDeleted) {
            log.info("쪽지 번호 {}가 성공적으로 삭제되었습니다.", noteboxReceiveNo);
            return "redirect:/centerMypage/noteboxReceiveList"; // 쪽지 리스트 페이지로 리다이렉트
        } else {
            log.error("쪽지 번호 {} 삭제 실패", noteboxReceiveNo);
            model.addAttribute("errorMessage", "쪽지 삭제에 실패했습니다.");
            return "centerMypage/noteboxError"; // 에러 페이지로 이동
        }
    }





}
