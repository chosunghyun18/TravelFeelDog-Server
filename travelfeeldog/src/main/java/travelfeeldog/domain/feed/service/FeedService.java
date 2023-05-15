package travelfeeldog.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travelfeeldog.domain.feed.dao.FeedImagesRepository;
import travelfeeldog.domain.feed.dao.FeedRepository;
import travelfeeldog.domain.feed.dao.FeedTagRepository;
import travelfeeldog.domain.feed.model.Feed;
import travelfeeldog.domain.feed.model.FeedImages;
import travelfeeldog.domain.feed.model.FeedTag;
import travelfeeldog.domain.member.dao.MemberRepository;
import travelfeeldog.domain.member.model.Member;
import travelfeeldog.domain.tag.dao.TagRepository;
import travelfeeldog.domain.tag.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final FeedImagesRepository feedImagesRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Feed postFeed(String writerToken,
                         String title,
                         String body,
                         List<String> feedImagesUrls,
                         List<String> tagContents) {
        Member writer = memberRepository.findByToken(writerToken).get();
        List<Tag> tags = new ArrayList<>();


        for(String tagContent : tagContents) {
            Optional<Tag> tag = tagRepository.findByTagContent(tagContent);
            if(tag.isPresent()) {
                tags.add(tag.get());
            }
            else {
                tags.add(tagRepository.save(tagContent));
            }
        }

        Feed result;
        int imagesExist = feedImagesUrls.isEmpty() ? 2 : 0; //10(binary)
        int tagsExist = tagContents.isEmpty() ? 1 : 0; //01(binary)
        switch(imagesExist | tagsExist) {
            case 3:
                result = feedRepository.save(writer, feedImagesUrls, title, body, tags);
                break;
            case 2:
                result = feedRepository.save(writer, feedImagesUrls, title, body);
                break;
            case 1:
                result = feedRepository.save(writer, title, body, tags);
                break;
            default:
                result = feedRepository.save(writer,
                        title,
                        body);
                break;
        }
        return result;
    }

    public Feed getFeedStaticsById(String id) {
        return feedRepository.getFeedStaticData(Long.parseLong(id))
                .orElseThrow(() -> new IllegalStateException("search error"));
    }
    public Optional<Feed> getFeedStaticsById(Long id) {
        return feedRepository.getFeedStaticData(id);
    }

    @Transactional
    public void deleteFeed(String id) { feedRepository.deleteById(Long.parseLong(id)); }

    public List<Feed> getListAll() {
        List<Feed> feeds = feedRepository.getListAll();
        return feeds;
    }

    public List<Feed> getListByNickName(String nickName) {
        List<Feed> feeds = feedRepository.findByNickName(nickName);
        return feeds;
    }
}
/*
*내일 할일 : feed Post 동작 확인
* 현재까지 작업한대로면 이제 url과 tag 다 들어가져야 함
* 
 */